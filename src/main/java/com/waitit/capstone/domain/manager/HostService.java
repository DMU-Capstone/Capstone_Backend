package com.waitit.capstone.domain.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waitit.capstone.domain.image.ImageService;
import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import com.waitit.capstone.domain.manager.dto.WaitingListDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.redisson.api.RBatch;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class HostService {
    private final HostRepository hostRepository;
    private final StringRedisTemplate redisTemplate;
    private final HostMapper hostMapper;
    private final ImageService imageService;
    private final RedissonClient redissonClient;
    private static final String ACTIVE_HOSTS_KEY = "active:hosts";
    private static final String SORTED_HOSTS_KEY = "sorted:hosts";
    private String getWaitListKey(Long hostId) {
        return "waitList:" + hostId;
    }
    //호스트 정보 저장
    @Transactional
    public void saveHost(HostRequest request,List<MultipartFile> hostImages) throws IOException {

        Host host = hostMapper.toEntity(request);
        Host saved = hostRepository.save(host);

        if (hostImages != null) {
            for (MultipartFile file : hostImages) {
                // imageService 에서 HostImage 엔티티 생성 & 파일 저장, DB에는 아직 영속화 안 됨
                HostImage img = imageService.uploadHost(saved.getId(), file);
                // Host.addImage 으로 images 리스트에 추가(양방향 세팅)
                saved.addImage(img);
            }
            // cascade = ALL 이므로 save 한번 더 해 주면 이미지도 같이 저장
            hostRepository.save(saved);
        }


        // 1. 활성 호스트 Set에 호스트 ID 추가
        RSet<Long> activeHosts = redissonClient.getSet(ACTIVE_HOSTS_KEY);
        activeHosts.add(host.getId());

        // 2. 최신 정렬용 ZSet에 추가 (Score: 현재 시간)
        RScoredSortedSet<Long> sortedHosts = redissonClient.getScoredSortedSet(SORTED_HOSTS_KEY);
        sortedHosts.add(System.currentTimeMillis(), host.getId());
    }

    // 호스트 세션 비활성화
    @Transactional
    public void deactivateHost(Long hostId) {
        // RBatch를 사용해 여러 명령을 원자적으로 실행
        RBatch batch = redissonClient.createBatch();

        // 1. 활성 호스트 Set에서 호스트 ID 제거
        batch.getSet(ACTIVE_HOSTS_KEY).removeAsync(hostId);
        // 2. 정렬용 Set에서도 호스트 ID 제거
        batch.getScoredSortedSet(SORTED_HOSTS_KEY).removeAsync(hostId);
        // 3. 해당 호스트의 대기열 데이터도 삭제
        batch.getScoredSortedSet(getWaitListKey(hostId)).deleteAsync();

        batch.execute();
    }

    //요청받은 아이디로 db에 호스트 조회
    public HostResponse getHost(Long id) {
        Host host = hostRepository.findHostById(id)
                .orElseThrow(() -> new IllegalArgumentException("Host not found with id: " + id));
        return hostMapper.hostToDto(host);
    }

    public List<SessionListDto> getAllSessions() {
        RSet<Long> activeHosts = redissonClient.getSet(ACTIVE_HOSTS_KEY);
        Set<Long> activeIds = activeHosts.readAll();

        if (activeIds.isEmpty()) return List.of();

        List<Host> hosts = hostRepository.findAllById(activeIds);

        return hosts.stream().map(host -> {
            String imgUrl = host.getImages().stream().findFirst().map(HostImage::getImgPath).orElse(null);

            // 대기열 크기를 RScoredSortedSet의 size()로 조회
            int waiting = redissonClient.getScoredSortedSet(getWaitListKey(host.getId())).size();

            return SessionListDto.builder()
                    .hostId(host.getId())
                    .hostName(host.getHostName())
                    .imgUrl(imgUrl)
                    .waitingCount(waiting)
                    .estimatedTime(calculateEstimatedTime(host.getStartTime(), host.getEndTime()))
                    .build();
        }).collect(Collectors.toList());
    }




    // 예상 시간 계산 메소드
    private String calculateEstimatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        return null;
    }
    //웨이팅 리스트 조회
    public List<WaitingListDto> getQueueListByHostId(Long hostId) {
        String key = getWaitListKey(hostId);

        // Codec을 사용하여 QueueDto 객체로 직접 작업
        RScoredSortedSet<QueueDto> queue = redissonClient.getScoredSortedSet(key, new JsonJacksonCodec(
                QueueDto.class.getClassLoader()));

        // 모든 대기열 멤버(QueueDto 객체)를 Score 순서대로 가져옴
        Collection<QueueDto> dtoList = queue.readAll();

        return hostMapper.queueToWaiting(new ArrayList<>(dtoList));
    }

    //트렌드 호스트
    public List<SessionListDto> findTrendHost(int count){
        RScoredSortedSet<Long> sortedHosts = redissonClient.getScoredSortedSet(SORTED_HOSTS_KEY);

        // 1. 결과를 담을 비어있는 List를 먼저 생성합니다.
        List<Long> latestHostIds = new ArrayList<>();

        // 2. revRangeTo 메소드를 호출하여 위에서 만든 List에 결과를 채워넣습니다.
        sortedHosts.revRangeTo(latestHostIds.toString(), 0, count - 1);

        if (latestHostIds.isEmpty()) return List.of();

        List<Host> hosts = hostRepository.findAllById(latestHostIds);

        // Redis에서 조회한 순서대로 DB 조회 결과를 정렬
        List<Host> sorted = latestHostIds.stream()
                .flatMap(id -> hosts.stream().filter(h -> h.getId().equals(id)))
                .toList();

        return sorted.stream().map(host -> {
            String imgUrl = host.getImages().stream().findFirst().map(HostImage::getImgPath).orElse(null);
            int waiting = redissonClient.getScoredSortedSet(getWaitListKey(host.getId())).size();
            return SessionListDto.builder()
                    .hostId(host.getId())
                    .hostName(host.getHostName())
                    .imgUrl(imgUrl)
                    .waitingCount(waiting)
                    .estimatedTime(calculateEstimatedTime(host.getStartTime(), host.getEndTime()))
                    .build();
        }).collect(Collectors.toList());
    }
}

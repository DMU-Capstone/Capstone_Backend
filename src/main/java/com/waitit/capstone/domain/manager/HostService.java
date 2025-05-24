package com.waitit.capstone.domain.manager;

import com.waitit.capstone.domain.image.ImageService;
import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
public class HostService {
    private final HostRepository hostRepository;
    private final StringRedisTemplate redisTemplate;
    private final HostMapper hostMapper;
    private static final String ACTIVE_HOSTS_KEY = "active:hosts";
    private final ImageService imageService;
    public boolean hostExist(Long id) {
        return true;
    }

    //호스트 정보 저장
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


        String key = "waitList" + host.getId();
        //세션 등록
        redisTemplate.opsForList().rightPush(key, host.getHostName());
        // 활성 호스트 Set 호스트 ID 추가
        redisTemplate.opsForSet().add(ACTIVE_HOSTS_KEY, host.getId().toString());
    }

    // 호스트 세션 비활성화
    public void deactivateHost(Long hostId) {
        // 활성 호스트 Set에서 호스트 ID 제거
        redisTemplate.opsForSet().remove(ACTIVE_HOSTS_KEY, hostId.toString());
    }

    //요청받은 아이디로 db에 호스트 조회
    public HostResponse getHost(Long id) {
        Host host = hostRepository.findHostById(id);
        return hostMapper.hostToDto(host);
    }

    public List<SessionListDto> getAllSessions() {
        Set<String> activeIds = redisTemplate.opsForSet().members(ACTIVE_HOSTS_KEY);
        if (activeIds == null || activeIds.isEmpty()) return List.of();

        List<Long> ids = activeIds.stream().map(Long::valueOf).toList();
        List<Host> hosts = hostRepository.findAllById(ids);

        return hosts.stream().map(host -> {
            // 첫 번째 이미지 URL 꺼내기
            String imgUrl = host.getImages().stream()
                    .findFirst()
                    .map(HostImage::getImgPath)
                    .orElse(null);

            int waiting = Optional.ofNullable(redisTemplate.opsForList()
                            .size("waitList" + host.getId()))
                    .map(Long::intValue)
                    .orElse(0);

            return SessionListDto.builder()
                    .hostId(host.getId())
                    .hostName(host.getHostName())
                    .imgUrl(imgUrl)
                    .waitingCount(waiting)
                    .estimatedTime(calculateEstimatedTime(host.getStartTime(), host.getEndTime()))
                    .build();
        }).toList();
    }

    // 호스트가 활성 상태인지 확인
    public boolean isHostActive(Long hostId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ACTIVE_HOSTS_KEY, hostId.toString()));
    }

    // 예상 시간 계산 메소드
    private String calculateEstimatedTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return "미정";
        }

        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();

        if (hours > 0) {
            return hours + "시간 " + (minutes > 0 ? minutes + "분" : "");
        } else {
            return minutes + "분";
        }
    }
}

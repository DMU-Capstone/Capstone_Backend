package com.waitit.capstone.domain.manager;

import com.waitit.capstone.domain.manager.dto.HostRequest;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.dto.SessionListDto;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class HostService {
    private final HostRepository hostRepository;
    private final StringRedisTemplate redisTemplate;
    private final HostMapper hostMapper;
    private static final String ACTIVE_HOSTS_KEY = "active:hosts";

    public boolean hostExist(Long id) {
        return true;
    }

    //호스트 정보 저장
    public void saveHost(HostRequest request) {
        Host host = hostMapper.toEntity(request);
        String key = "waitList" + host.getId();
        hostRepository.save(host);
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
        List<SessionListDto> result = new ArrayList<>();

        // Redis Set에서 활성 호스트 ID 목록 가져오기
        Set<String> activeHostIds = redisTemplate.opsForSet().members(ACTIVE_HOSTS_KEY);

        if (activeHostIds != null && !activeHostIds.isEmpty()) {
            // 문자열 ID를 Long 변환
            List<Long> hostIds = activeHostIds.stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            // 활성 호스트만 DB 조회
            List<Host> activeHosts = hostRepository.findAllById(hostIds);

            for (Host host : activeHosts) {
                String waitListKey = "waitList" + host.getId();

                // 대기 인원 수 계산
                Long waitingCount = redisTemplate.opsForList().size(waitListKey);

                // 예상 시간 계산 (시작 시간부터 종료 시간까지)
                String estimatedTime = calculateEstimatedTime(host.getStartTime(), host.getEndTime());

                // DTO 생성 및 추가
                SessionListDto sessionDto = SessionListDto.builder()
                        .hostId(host.getId())
                        .hostName(host.getHostName())
                        .imgUrl(host.getImgUrl())
                        .estimatedTime(estimatedTime)
                        .waitingCount(waitingCount != null ? waitingCount.intValue() : 0)
                        .build();

                result.add(sessionDto);
            }
        }

        return result;
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

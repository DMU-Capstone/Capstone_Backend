package com.waitit.capstone.domain.queue.service;

import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.HostMapper;
import com.waitit.capstone.domain.queue.dto.MyQueueStatusResponse;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor // HostRepository, HostMapper 주입을 위해 추가
public class QueueService {

    private final Map<Long, Deque<QueueDto>> waitQueues = new ConcurrentHashMap<>();
    private final Map<Long, Map<QueueDto, Long>> postponeQueues = new ConcurrentHashMap<>();

    private final HostRepository hostRepository;
    private final HostMapper hostMapper;

    private static final long POSTPONE_DURATION_MILLIS = 10 * 60 * 1000L; // 10분
    private static final int AVG_WAIT_TIME_PER_PERSON = 5; // 1인당 평균 대기시간 (분)

    public boolean isHostActive(Long hostId) {
        return waitQueues.containsKey(hostId);
    }

    public int registerQueue(Long hostId, QueueDto dto) {
        Deque<QueueDto> queue = waitQueues.computeIfAbsent(hostId, k -> new LinkedList<>());

        boolean isDuplicate = queue.stream()
                .anyMatch(userInQueue -> userInQueue.getPhoneNumber().equals(dto.getPhoneNumber()));

        if (isDuplicate) {
            throw new IllegalStateException("이미 해당 가게의 대기열에 등록된 전화번호입니다.");
        }

        queue.add(dto);
        return queue.size();
    }

    public int getMyPosition(Long hostId, QueueDto myDto) {
        Deque<QueueDto> queue = waitQueues.get(hostId);
        if (queue == null) {
            return 0; // 대기열 없음
        }

        int position = 0;
        for (QueueDto userInQueue : queue) {
            position++;
            if (userInQueue.getPhoneNumber().equals(myDto.getPhoneNumber())) {
                return position;
            }
        }
        return 0; // 대기열에 사용자가 없음
    }

    public void deleteMyRegister(Long hostId, QueueDto dto) {
        Deque<QueueDto> queue = waitQueues.get(hostId);
        if (queue != null) {
            queue.removeIf(userInQueue -> userInQueue.getPhoneNumber().equals(dto.getPhoneNumber()));
        }
    }

    public void postpone(Long hostId, QueueDto dto) {
        Deque<QueueDto> queue = waitQueues.get(hostId);
        if (queue == null) {
            return;
        }
        synchronized (queue) {
            boolean removed = queue.removeIf(userInQueue -> userInQueue.getPhoneNumber().equals(dto.getPhoneNumber()));
            if (removed) {
                Map<QueueDto, Long> postponeMap = postponeQueues.computeIfAbsent(hostId, k -> new ConcurrentHashMap<>());
                long expirationTimestamp = System.currentTimeMillis() + POSTPONE_DURATION_MILLIS;
                postponeMap.put(dto, expirationTimestamp);
            }
        }
    }

    public void deletePostpone(Long hostId, QueueDto dto) {
        Map<QueueDto, Long> postponeMap = postponeQueues.get(hostId);
        if (postponeMap != null) {
            postponeMap.remove(dto);
        }
    }

    public void deactivateQueue(Long hostId) {
        waitQueues.remove(hostId);
        postponeQueues.remove(hostId);
    }

    public Set<Long> getActiveHostIds() {
        return waitQueues.keySet();
    }

    public List<QueueDto> getQueueByHostId(Long hostId) {
        Deque<QueueDto> queue = waitQueues.get(hostId);
        return (queue != null) ? new ArrayList<>(queue) : Collections.emptyList();
    }

    /**
     * [추가] 나의 실시간 대기 현황을 조회합니다.
     */
    public MyQueueStatusResponse getMyQueueStatus(String phoneNumber) {
        for (Map.Entry<Long, Deque<QueueDto>> entry : waitQueues.entrySet()) {
            Long hostId = entry.getKey();
            Deque<QueueDto> queue = entry.getValue();

            int position = 0;
            for (QueueDto userInQueue : queue) {
                position++;
                if (userInQueue.getPhoneNumber().equals(phoneNumber)) {
                    // 사용자를 찾음! 이제 정보 조합
                    HostResponse hostInfo = hostRepository.findHostById(hostId)
                            .map(hostMapper::hostToDto)
                            .orElseThrow(() -> new IllegalStateException("대기열에 있는 가게 정보를 찾을 수 없습니다."));

                    int totalWaitingCount = queue.size();
                    String estimatedTime = (position * AVG_WAIT_TIME_PER_PERSON) + "분";

                    return MyQueueStatusResponse.builder()
                            .message("현재 대기 중입니다.")
                            .isWaiting(true)
                            .myWaitingNumber(position)
                            .totalWaitingCount(totalWaitingCount)
                            .estimatedWaitTime(estimatedTime)
                            .hostInfo(hostInfo)
                            .build();
                }
            }
        }
        // 모든 대기열을 찾아도 사용자를 찾지 못함
        return MyQueueStatusResponse.builder()
                .message("현재 대기 중인 가게가 없습니다.")
                .isWaiting(false)
                .myWaitingNumber(0)
                .totalWaitingCount(0)
                .estimatedWaitTime("-")
                .hostInfo(null)
                .build();
    }
}

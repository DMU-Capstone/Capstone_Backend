package com.waitit.capstone.domain.queue.service;

import com.waitit.capstone.domain.queue.dto.QueueDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QueueService {

    private final Map<Long, Deque<QueueDto>> waitQueues = new ConcurrentHashMap<>();
    private final Map<Long, Map<QueueDto, Long>> postponeQueues = new ConcurrentHashMap<>();

    private static final long POSTPONE_DURATION_MILLIS = 10 * 60 * 1000L; // 10분

    public boolean isHostActive(Long hostId) {
        return waitQueues.containsKey(hostId);
    }

    /**
     * [수정] 전화번호 중복 등록 방지 로직 추가
     */
    public int registerQueue(Long hostId, QueueDto dto) {
        Deque<QueueDto> queue = waitQueues.computeIfAbsent(hostId, k -> new LinkedList<>());

        // 중복 검사: 큐에 이미 같은 전화번호가 있는지 확인
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
}

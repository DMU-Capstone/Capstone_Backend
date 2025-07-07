package com.waitit.capstone.domain.queue;

import com.waitit.capstone.domain.queue.dto.QueueDto;
import lombok.RequiredArgsConstructor;
import org.redisson.api.BatchOptions;
import org.redisson.api.BatchOptions.ExecutionMode;
import org.redisson.api.RBatch;
import org.redisson.api.RList;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final RedissonClient redissonClient;
    private static final String ACTIVE_HOSTS_KEY = "active:hosts";
    private static final long POSTPONE_DURATION_MILLIS = 10 * 60 * 1000L;
    //host 존재 여부 확인
    private boolean isHostActive(Long hostId) {
        RSet<Long> activeHosts = redissonClient.getSet(ACTIVE_HOSTS_KEY);
        return activeHosts.contains(hostId);
    }
    private String getWaitListKey(Long hostId) {
        return "waitList:" + hostId;
    }
    private String getPostponeHostsKey(Long hostId) {
        return "postponeList:" + hostId;
    }


    public int registerQueue(Long id, QueueDto dto){
        if (!isHostActive(id)) {
            throw new IllegalStateException("비활성화된 호스트입니다.");
        }

        // 줄 세우기 - Redis 리스트에 DTO 추가
        RList<QueueDto> queue = redissonClient.getList(getWaitListKey(id));

        queue.add(dto);

        return queue.size();
    }

    public int getMyPosition(Long hostId, QueueDto myDto){
        RList<QueueDto> queue = redissonClient.getList(getWaitListKey(hostId));
        return queue.indexOf(myDto)+1;

    }

    public void deleteMyRegister(Long id, QueueDto dto) {
        RList<QueueDto> list = redissonClient.getList(getWaitListKey(id));
        list.remove(dto);
    }

    public void postpone(Long id, QueueDto dto){
        // 원자성 보장을 위해 RBatch 사용
        BatchOptions options = BatchOptions.defaults()
                .executionMode(ExecutionMode.IN_MEMORY_ATOMIC);

        RBatch batch = redissonClient.createBatch(options);

        // 1. 대기열에서 제거
        batch.getList(getWaitListKey(id)).removeAsync(dto);

        // 2. 미루기 목록에 추가
        long expirationTimestamp = System.currentTimeMillis() + POSTPONE_DURATION_MILLIS;
        batch.getScoredSortedSet(getPostponeHostsKey(id)).addAsync(expirationTimestamp, dto);

        // 두 작업 동시 실행
        batch.execute();
    }

    public void deletePostpone(Long id, QueueDto dto){
        RScoredSortedSet<QueueDto> postponeSet = redissonClient.getScoredSortedSet(getPostponeHostsKey(id));
        postponeSet.remove(dto);
    }
}

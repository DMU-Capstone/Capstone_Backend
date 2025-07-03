package com.waitit.capstone.domain.queue;

import com.waitit.capstone.domain.queue.dto.QueueDto;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final StringRedisTemplate redisTemplate;
    private final RedissonClient redissonClient;
    private static final String ACTIVE_HOSTS_KEY = "active:hosts";

    //host 존재 여부 확인
    private boolean isHostActive(Long hostId) {
        RSet<Long> activeHosts = redissonClient.getSet(ACTIVE_HOSTS_KEY);
        return activeHosts.contains(hostId);
    }
    private String getWaitListKey(Long hostId) {
        return "waitList:" + hostId;
    }

    public int registerQueue(Long id, QueueDto dto){
        if (!isHostActive(id)) {
            throw new IllegalStateException("비활성화된 호스트입니다.");
        }

        // 줄 세우기 - Redis 리스트에 DTO 추가
        RList<QueueDto> queue = redissonClient.getList(getWaitListKey(id));

        if (queue == null) {
            throw new RuntimeException("대기열 등록 실패");
        }

        queue.add(dto);

        return queue.size()-1;
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

    }
    public void deletePostpone(Long id, QueueDto dto){

    }
}

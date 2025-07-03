package com.waitit.capstone.domain.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisCallback;
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

        String key = getWaitListKey(id);

        // 줄 세우기 - Redis 리스트에 DTO 추가
        RList<QueueDto> queue = redissonClient.getList(key);

        if (queue == null) {
            throw new RuntimeException("대기열 등록 실패");
        }

        queue.add(dto);

        return queue.size()-1;
    }

    public int getMyPosition(Long hostId, QueueDto myDto){

        String key = getWaitListKey(hostId);
        RList<QueueDto> queue = redissonClient.getList(key);

        return queue.indexOf(myDto)+1;

    }

    public void deleteMyRegister(Long id, QueueDto dto) {

        String key    =  getWaitListKey(id);
        RList<QueueDto> list = redissonClient.getList(key);

        list.remove(dto);
    }

}

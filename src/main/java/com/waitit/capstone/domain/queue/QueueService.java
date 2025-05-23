package com.waitit.capstone.domain.queue;

import com.waitit.capstone.domain.manager.HostService;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final StringRedisTemplate redisTemplate;
    //host 존재 여부 확인
        private boolean isHostActive(Long hostId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember("active:hosts", hostId.toString()));
    }
    public void registerQueue(Long id, QueueDto dto){

    }

}

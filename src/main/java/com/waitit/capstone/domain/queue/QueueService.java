package com.waitit.capstone.domain.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waitit.capstone.domain.manager.HostService;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisCallback;
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
    private String convertDtoToString(QueueDto dto) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("QueueDto 직렬화 실패", e);
        }
        }
    private QueueDto convertStringToDto(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, QueueDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("QueueDto 역직렬화 실패", e);
        }
    }
    public int registerQueue(Long id, QueueDto dto){
        if (!isHostActive(id)) {
            throw new IllegalStateException("비활성화된 호스트입니다.");
        }

        String key = "waitList" + id;

        // 줄 세우기 - Redis 리스트에 DTO 추가
        Long size = redisTemplate.opsForList().rightPush(key, convertDtoToString(dto));

        if (size == null) {
            throw new RuntimeException("대기열 등록 실패");
        }

        return size.intValue()-1;
    }
    public int getMyPosition(Long hostId, QueueDto myDto){
        String key = "waitList" + hostId;
        byte[] rawKey    = key.getBytes(StandardCharsets.UTF_8);
        byte[] rawMember = convertDtoToString(myDto).getBytes(StandardCharsets.UTF_8);

        Long pos = redisTemplate.execute((RedisCallback<Long>) conn ->
                // Redis 7.0 이상에서 지원하는 LPOS
                conn.lPos(rawKey, rawMember)
        );

        // null이면 리스트에 없음
        return pos != null ? pos.intValue() : -1;

    }
}

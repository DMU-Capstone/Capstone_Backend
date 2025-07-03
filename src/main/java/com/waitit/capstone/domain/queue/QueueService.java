package com.waitit.capstone.domain.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
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
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(ACTIVE_HOSTS_KEY, hostId.toString()));
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

    public void deleteMyRegister(Long id, QueueDto dto) {
        String key    = "waitList" + id;
        String member = convertDtoToString(dto);

        // 1) 삭제 전 내 위치를 알아두기 (0-based)
        int pos = getMyPosition(id, dto);
        if (pos < 0) {
            throw new IllegalStateException("대기열에 등록되어 있지 않습니다.");
        }

        // 2) Redis LREM 명령으로 리스트에서 해당 항목(첫 번째)을 삭제
        //    remove(key, count, value) → count=1이면 첫 번째 매칭만 지움
        Long removedCount = redisTemplate.opsForList().remove(key, 1, member);
        if (removedCount == null || removedCount == 0) {
            throw new RuntimeException("대기열 제거에 실패했습니다.");
        }

    }

    /**
     * 내 QueueDto를 현재 위치에서 offset만큼 뒤로 이동시킵니다.
     * @param hostId   호스트 ID
     * @param dto      내 QueueDto
     * @param offset   뒤로 미룰 칸 수 (0 이하면 아무 동작 안 함)
     */
    public void postpone(Long hostId, QueueDto dto, int offset) {
        if (offset <= 0)  throw new IllegalStateException("1칸 이상 칸을 미뤄야 합니다.");

        String key    = "waitList" + hostId;
        String member = convertDtoToString(dto);

        // 1) 현재 위치 조회
        int oldPos = getMyPosition(hostId, dto);
        if (oldPos < 0) {
            throw new IllegalStateException("대기열에 등록되어 있지 않습니다.");
        }

        // 2) 리스트에서 하나만 제거
        Long removed = redisTemplate.opsForList().remove(key, 1, member);
        if (removed == null || removed == 0) {
            throw new RuntimeException("대기열에서 제거 실패");
        }

        // 3) 제거 후 남은 리스트 전체 조회
        List<String> list = redisTemplate.opsForList().range(key, 0, -1);
        int size = (list != null ? list.size() : 0);

        // 4) 새 목표 인덱스 계산
        int newPos = Math.min(oldPos + offset, size);

        // 5) 삽입
        if (newPos >= size) {
            // 끝에 붙이기
            redisTemplate.opsForList().rightPush(key, member);
        } else {
            // 리스트 내에서 삽입할 기준 요소(pivot) 찾기
            String pivot = list.get(newPos);
            // pivot 앞에(member가 pivot의 index 위치로) 삽입
            redisTemplate.opsForList().leftPush(key, pivot, member);
        }

    }
}

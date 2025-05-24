package com.waitit.capstone.domain.queue;

import com.waitit.capstone.domain.queue.dto.QueResponseDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.dto.QueueRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;
    private final QueueMapper queueMapper;
    private final Map<Long, List<DeferredResult<ResponseEntity<Integer>>>> waiters = new ConcurrentHashMap<>();

    @PostMapping("/{id}")
    public ResponseEntity<?> registerQueue(@PathVariable Long id, @RequestBody QueueRequest queueRequest){
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int index = queueService.registerQueue(id,dto);
        QueResponseDto responseDto = new QueResponseDto("대기열 등록 완료",index);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    @GetMapping("/{id}/position/long-poll")
    public DeferredResult<ResponseEntity<Integer>> getMyPositionLongPolling(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest) {

        // 1) 클라이언트 요청에 붙일 DeferredResult 생성 (타임아웃 30초, 기본값 204 No Content)
        DeferredResult<ResponseEntity<Integer>> dr =
                new DeferredResult<>(30_000L, ResponseEntity.noContent().build());

        // 2) 타임아웃 또는 완료 시 반드시 대기자 목록에서 제거
        dr.onCompletion(() -> waiters.getOrDefault(id, List.of()).remove(dr));

        // 3) 대기자 맵에 추가
        waiters
                .computeIfAbsent(id, key -> new CopyOnWriteArrayList<>())
                .add(dr);

        return dr;
    }

    // 기존 registerQueue 메서드 안에, 대기열 변화가 생길 때마다 호출될 알림용 헬퍼
    private void notifyPositionChange(Long hostId, QueueDto dto) {
        List<DeferredResult<ResponseEntity<Integer>>> list = waiters.get(hostId);
        if (list == null || list.isEmpty()) return;

        int newPos = queueService.getMyPosition(hostId, dto);  // LPOS 로 조회한 0-based 위치
        ResponseEntity<Integer> body = ResponseEntity.ok(newPos + 1);  // 1-based 로 바꿔서 반환

        // 모든 대기 중인 요청에 결과 세팅
        list.forEach(dr -> dr.setResult(body));
        // 그리고 다시 폴링할 수 있게 클리어
        list.clear();
    }


    //대기열 입장 일림
    //대기열 입장 완료 확인
    //어드민 대기열 실시간 확인
    //호스트 대기열 확인
    //호스트등록 폼 데이터 변경

}

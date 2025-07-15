package com.waitit.capstone.domain.queue;

import com.waitit.capstone.domain.queue.dto.QueResponseDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.dto.QueueRequest;

import com.waitit.capstone.domain.queue.service.QueueService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;
    private final QueueMapper queueMapper;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    // 최대 대기 시간 (ms)
    private static final long TIMEOUT = 30_000L;   // 30초
    private static final long POLL_INTERVAL = 1_000L; // 1초

    //대기열 등록
    @PostMapping("/{id}")
    public ResponseEntity<?> registerQueue(@PathVariable Long id, @RequestBody QueueRequest queueRequest, HttpServletRequest request){

        String token = request.getHeader("access");
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int index = 0;
        index = queueService.registerQueue(id,dto);


        QueResponseDto responseDto = new QueResponseDto("대기열 등록 완료",index);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    //내 대기열 순번 확인
    @GetMapping("/{id}/position/")
    public DeferredResult<ResponseEntity<QueResponseDto>> getMyPosition(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest,
            @RequestParam(name = "lastPos", required = false) Integer lastPos) {
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int      initialPos = queueService.getMyPosition(id, dto);
        int      clientLast = (lastPos != null ? lastPos : initialPos);

        DeferredResult<ResponseEntity<QueResponseDto>> result = new DeferredResult<>(TIMEOUT);

        /* ① 타임아웃 시 기본 응답 */
        result.onTimeout(() -> {
            QueResponseDto body = new QueResponseDto("변동 없음(타임아웃)", clientLast);
            result.setResult(ResponseEntity.ok(body));
        });

        /* ② 1초마다 순번 변동 체크 */
        scheduler.scheduleAtFixedRate(() -> {
            if (result.isSetOrExpired()) return;   // 이미 응답했으면 skip

            int current = queueService.getMyPosition(id, dto);
            if (current != clientLast) {           // 순번 변동!
                QueResponseDto body = new QueResponseDto("순번 변경!", current);
                result.setResult(ResponseEntity.ok(body));
            }
        }, 0, POLL_INTERVAL, TimeUnit.MILLISECONDS);

        return result;
    }

    //대기열 입장 취소
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMyQueue(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest){
            QueueDto dto = queueMapper.requestToDto(queueRequest);
            queueService.deleteMyRegister(id,dto);
            return ResponseEntity.status(HttpStatus.OK).body("대기 취소 완료");
    }

    //대기열 미루기 큐에 인원 추가
    @PostMapping("/{id}/postpone/")
    public ResponseEntity<?> queuePostpone(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest){
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        queueService.postpone(id,dto);
        return ResponseEntity.status(HttpStatus.OK).body("대기 미룸");
    }
    //미루기 큐에서 입장하기
    @PostMapping("/{id}/admit")
    public ResponseEntity<?> admitFromPostpone(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest) {
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        queueService.deletePostpone(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("미루기 큐에서 입장 처리되었습니다.");
    }

}

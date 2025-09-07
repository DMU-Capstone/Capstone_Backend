package com.waitit.capstone.domain.queue.controller;

import com.waitit.capstone.domain.queue.QueueMapper;
import com.waitit.capstone.domain.queue.dto.QueResponseDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.dto.QueueRequest;

import com.waitit.capstone.domain.queue.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "대기열 API", description = "가게 대기열 관련 API")
public class QueueController {
    private final QueueService queueService;
    private final QueueMapper queueMapper;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    // 최대 대기 시간 (ms)
    private static final long TIMEOUT = 30_000L;   // 30초
    private static final long POLL_INTERVAL = 1_000L; // 1초

    @Operation(summary = "대기열 등록", description = "특정 가게의 대기열에 사용자를 등록합니다.")
    @PostMapping("/{id}")
    public ResponseEntity<?> registerQueue(@PathVariable Long id, @RequestBody QueueRequest queueRequest, HttpServletRequest request){

        String token = request.getHeader("access");
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int index = 0;
        index = queueService.registerQueue(id,dto);


        QueResponseDto responseDto = new QueResponseDto("대기열 등록 완료",index);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @Operation(summary = "내 대기 순번 확인 (Long Polling)", description = "자신의 대기 순번을 확인합니다. 순번 변경이 있을 때까지 연결을 유지합니다.")
    @GetMapping("/{id}/position/")
    public DeferredResult<ResponseEntity<QueResponseDto>> getMyPosition(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest,
            @RequestParam(name = "lastPos", required = false) Integer lastPos) {
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int      initialPos = queueService.getMyPosition(id, dto);
        int      clientLast = (lastPos != null ? lastPos : initialPos);

        DeferredResult<ResponseEntity<QueResponseDto>> result = new DeferredResult<>(TIMEOUT);

        result.onTimeout(() -> {
            QueResponseDto body = new QueResponseDto("변동 없음(타임아웃)", clientLast);
            result.setResult(ResponseEntity.ok(body));
        });

        scheduler.scheduleAtFixedRate(() -> {
            if (result.isSetOrExpired()) return;

            int current = queueService.getMyPosition(id, dto);
            if (current != clientLast) { 
                QueResponseDto body = new QueResponseDto("순번 변경!", current);
                result.setResult(ResponseEntity.ok(body));
            }
        }, 0, POLL_INTERVAL, TimeUnit.MILLISECONDS);

        return result;
    }

    @Operation(summary = "대기열 등록 취소", description = "대기열 등록을 취소합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMyQueue(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest){
            QueueDto dto = queueMapper.requestToDto(queueRequest);
            queueService.deleteMyRegister(id,dto);
            return ResponseEntity.status(HttpStatus.OK).body("대기 취소 완료");
    }

    @Operation(summary = "대기 순번 미루기", description = "자신의 대기 순번을 뒤로 미룹니다.")
    @PostMapping("/{id}/postpone/")
    public ResponseEntity<?> queuePostpone(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest){
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        queueService.postpone(id,dto);
        return ResponseEntity.status(HttpStatus.OK).body("대기 미룸");
    }

    @Operation(summary = "미루기 후 입장", description = "미루기 상태에서 다시 대기열에 입장합니다.")
    @PostMapping("/{id}/admit")
    public ResponseEntity<?> admitFromPostpone(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest) {
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        queueService.deletePostpone(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body("미루기 큐에서 입장 처리되었습니다.");
    }

}

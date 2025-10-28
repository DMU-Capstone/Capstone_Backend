package com.waitit.capstone.domain.queue.controller;

import com.waitit.capstone.domain.manager.dto.HostResponse;
import com.waitit.capstone.domain.manager.service.HostService;
import com.waitit.capstone.domain.queue.QueueMapper;
import com.waitit.capstone.domain.queue.dto.MyQueueStatusResponse;
import com.waitit.capstone.domain.queue.dto.QueResponseDto;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.dto.QueueRegistrationResponse;
import com.waitit.capstone.domain.queue.dto.QueueRequest;

import com.waitit.capstone.domain.queue.service.QueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Tag(name = "대기열 API", description = "가게 대기열 관련 API")
public class QueueController {
    private final QueueService queueService;
    private final QueueMapper queueMapper;
    private final HostService hostService;

    @Operation(summary = "대기열 등록", description = "특정 가게의 대기열에 사용자를 등록하고, 등록된 가게의 상세 정보를 함께 반환합니다.")
    @PostMapping("/{id}")
    public ResponseEntity<QueueRegistrationResponse> registerQueue(@PathVariable Long id, @RequestBody QueueRequest queueRequest, HttpServletRequest request){
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int waitingNumber = queueService.registerQueue(id, dto);

        HostResponse hostInfo = hostService.getHost(id);

        QueueRegistrationResponse response = QueueRegistrationResponse.builder()
                .message("대기열 등록에 성공했습니다.")
                .waitingNumber(waitingNumber)
                .hostInfo(hostInfo)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "내 대기 순번 즉시 확인", description = "자신의 현재 대기 순번을 즉시 확인합니다.")
    @GetMapping("/{id}/position/")
    public ResponseEntity<QueResponseDto> getMyPosition(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest) {
        
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int currentPosition = queueService.getMyPosition(id, dto);

        QueResponseDto responseDto = new QueResponseDto("현재 대기 순번입니다.", currentPosition);
        return ResponseEntity.ok(responseDto);
    }

    /**
     * [추가] 나의 실시간 대기 현황 조회 API
     */
    @Operation(summary = "나의 실시간 대기 현황 조회", description = "전화번호를 통해 현재 대기 중인 가게의 상세 현황을 조회합니다.")
    @GetMapping("/my-status")
    public ResponseEntity<MyQueueStatusResponse> getMyQueueStatus(@RequestParam String phoneNumber) {
        MyQueueStatusResponse response = queueService.getMyQueueStatus(phoneNumber);
        return ResponseEntity.ok(response);
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

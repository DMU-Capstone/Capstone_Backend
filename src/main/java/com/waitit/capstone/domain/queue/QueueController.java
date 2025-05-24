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
import org.springframework.web.bind.annotation.DeleteMapping;
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
    //대기열 등록
    @PostMapping("/{id}")
    public ResponseEntity<?> registerQueue(@PathVariable Long id, @RequestBody QueueRequest queueRequest){
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int index = queueService.registerQueue(id,dto);
        QueResponseDto responseDto = new QueResponseDto("대기열 등록 완료",index);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    //내 대기열 순번 확인
    @GetMapping("/{id}/position/")
    public ResponseEntity<?> getMyPosition(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest) {
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        int index =  queueService.getMyPosition(id,dto);
        QueResponseDto responseDto = new QueResponseDto("현재 내 대기열 순번은",index);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
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

    //두칸 뒤로 미룸
    @PostMapping("/{id}/postpone/")
    public ResponseEntity<?> queuePostpone(
            @PathVariable Long id,
            @RequestBody QueueRequest queueRequest){
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        queueService.postpone(id,dto,2);
        return ResponseEntity.status(HttpStatus.OK).body("대기 2칸 미룸");
    }

}

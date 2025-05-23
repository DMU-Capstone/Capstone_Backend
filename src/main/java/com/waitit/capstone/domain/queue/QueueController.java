package com.waitit.capstone.domain.queue;

import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.dto.QueueRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;
    private final QueueMapper queueMapper;

    @PostMapping("/{id}")
    public ResponseEntity<?> registerQueue(@PathVariable Long id, @RequestBody QueueRequest queueRequest){
        QueueDto dto = queueMapper.requestToDto(queueRequest);
        queueService.registerQueue(id,dto);
        return ResponseEntity.status(HttpStatus.OK).body("대기열 등록 완료");
    }
    //대기열 실시간 확인
    //대기열 입장 일림
    //대기열 입장 완료 확인
    //어드민 대기열 실시간 확인
    //호스트 대기열 확인
    //호스트등록 폼 데이터 변경

}

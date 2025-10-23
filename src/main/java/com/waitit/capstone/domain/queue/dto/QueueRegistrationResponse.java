package com.waitit.capstone.domain.queue.dto;

import com.waitit.capstone.domain.manager.dto.HostResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueRegistrationResponse {
    private String message;         // 응답 메시지
    private int waitingNumber;      // 나의 대기 번호
    private HostResponse hostInfo;  // 내가 등록한 가게의 상세 정보
}

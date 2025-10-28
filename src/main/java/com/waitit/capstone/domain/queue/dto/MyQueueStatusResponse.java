package com.waitit.capstone.domain.queue.dto;

import com.waitit.capstone.domain.manager.dto.HostResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyQueueStatusResponse {
    private String message;
    private boolean isWaiting;          // 현재 대기 중인지 여부
    private int myWaitingNumber;        // 나의 대기 순번 (대기 중이 아닐 경우 0)
    private int totalWaitingCount;      // 총 대기 인원
    private String estimatedWaitTime;   // 예상 대기 시간 (예: "15분")
    private HostResponse hostInfo;      // 내가 대기 중인 가게 정보
}

package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReturnRateResponse {
    private long totalVisitors;         // 총 방문자 수 (중복제거)
    private long newVisitors;           // 신규 방문자 수
    private long returningVisitors;     // 재방문자 수
    private double returnRate;          // 재방문율
}

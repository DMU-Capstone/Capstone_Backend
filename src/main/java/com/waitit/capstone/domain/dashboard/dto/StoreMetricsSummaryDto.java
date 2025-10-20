package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreMetricsSummaryDto {
    private long totalWaitlistCount;    // 총 대기열 수
    private long totalActualUsers;      // 총 실제 이용자 수
    private long totalDropouts;         // 총 이탈 수
    private double dropoutRate;         // 이탈률
    private double averageWaitTimeSeconds; // 첫 이용까지 걸린 시간 (평균)
}

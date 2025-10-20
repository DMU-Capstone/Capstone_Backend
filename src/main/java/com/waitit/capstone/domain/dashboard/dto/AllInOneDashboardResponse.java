package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AllInOneDashboardResponse {
    private StoreMetricsResponse storeMetrics;          // 1. 전체 스토어 지표
    private ReturnRateResponse returnRate;              // 3. 재방문율
    private List<WaitlistTrendHourlyData> waitlistTrend;  // 4. 예상 대기인원 추이
    private ReviewAndCancelStatsResponse reviewAndCancelStats; // 5. 리뷰 및 취소 사유
    private List<PeakAnalysisDataDto> peakAnalysis;       // 2. 피크 분석
}

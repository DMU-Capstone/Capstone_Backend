package com.waitit.capstone.domain.dashboard.controller;

import com.waitit.capstone.domain.dashboard.dto.*;
import com.waitit.capstone.domain.dashboard.service.DashboardServiceV2;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/dashboard")
@RequiredArgsConstructor
@Tag(name = "대시보드 API V2", description = "새로운 대시보드 통계 API")
public class DashboardControllerV2 {

    private final DashboardServiceV2 dashboardServiceV2;

    @GetMapping("/store-metrics")
    @Operation(summary = "(V2) 전체 스토어 지표 조회", description = "기간별 전체 및 시간대별 대기, 이용, 이탈 통계를 조회합니다.")
    public ResponseEntity<StoreMetricsResponse> getStoreMetrics(
            @RequestParam Long storeId,
            @RequestParam String dateRange) {
        StoreMetricsResponse metrics = dashboardServiceV2.getStoreMetrics(storeId, dateRange);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/waitlist-trend")
    @Operation(summary = "(V2) 예상 대기인원 추이 조회", description = "시간대별 대기열 등록 수와 실제 이용률을 조회합니다.")
    public ResponseEntity<List<WaitlistTrendHourlyData>> getWaitlistTrend(
            @RequestParam Long storeId,
            @RequestParam String dateRange) {
        List<WaitlistTrendHourlyData> trend = dashboardServiceV2.getWaitlistTrend(storeId, dateRange);
        return ResponseEntity.ok(trend);
    }

    @GetMapping("/return-rate")
    @Operation(summary = "(V2) 재방문율 조회", description = "기간별 신규 및 재방문 고객 비율을 조회합니다.")
    public ResponseEntity<ReturnRateResponse> getReturnRate(
            @RequestParam Long storeId,
            @RequestParam String dateRange) {
        ReturnRateResponse rate = dashboardServiceV2.getReturnRate(storeId, dateRange);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/review-and-cancel-stats")
    @Operation(summary = "(V2) 리뷰 및 대기 취소 사유 조회", description = "기간별 대기 취소 사유 통계(%)와 리뷰 목록을 조회합니다.")
    public ResponseEntity<ReviewAndCancelStatsResponse> getReviewAndCancelStats(
            @RequestParam Long storeId,
            @RequestParam String dateRange,
            @RequestParam(required = false) Integer ratingMin) {
        ReviewAndCancelStatsResponse stats = dashboardServiceV2.getReviewAndCancelStats(storeId, dateRange, ratingMin);
        return ResponseEntity.ok(stats);
    }

    // 피크 분석 API는 추가 구현이 필요합니다.
}

package com.waitit.capstone.domain.dashboard.controller;

import com.waitit.capstone.domain.dashboard.dto.*;
import com.waitit.capstone.domain.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "대시보드 API", description = "가게 대시보드 통계 및 관리 관련 API")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/visitors/statistics")
    @Operation(summary = "방문 고객 통계 조회", description = "기간별 신규/재방문 고객 비율 등 방문 통계를 조회합니다.")
    // TODO: 로그인한 사용자가 자신의 가게 정보만 볼 수 있도록 보안 로직 추가 필요
    public ResponseEntity<VisitorStatisticsResponse> getVisitorStatistics(
            @RequestParam Long storeId,
            @RequestParam String dateRange) {
        VisitorStatisticsResponse stats = dashboardService.getVisitorStatistics(storeId, dateRange);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/waiting/trends")
    @Operation(summary = "대기인원 추세 및 예측 조회", description = "기간별 시간대 실제/예측 대기 인원 추세를 조회합니다.")
    // TODO: 로그인한 사용자가 자신의 가게 정보만 볼 수 있도록 보안 로직 추가 필요
    public ResponseEntity<WaitingTrendsResponse> getWaitingTrends(
            @RequestParam Long storeId,
            @RequestParam String dateRange) {
        WaitingTrendsResponse trends = dashboardService.getWaitingTrends(storeId, dateRange);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/reports/summary")
    @Operation(summary = "리포트 분석 조회", description = "주간/월간 단위 방문자, 이탈률, 재방문률을 종합하여 조회합니다.")
    // TODO: 로그인한 사용자가 자신의 가게 정보만 볼 수 있도록 보안 로직 추가 필요
    public ResponseEntity<ReportSummaryResponse> getReportSummary(
            @RequestParam Long storeId,
            @RequestParam(defaultValue = "WEEKLY") String type) {
        ReportSummaryResponse summary = dashboardService.getReportSummary(storeId, type);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reviews/statistics")
    @Operation(summary = "리뷰 및 대기 취소 사유 조회", description = "기간별 대기 취소 사유 통계와 리뷰 목록을 조회합니다.")
    // TODO: 로그인한 사용자가 자신의 가게 정보만 볼 수 있도록 보안 로직 추가 필요
    public ResponseEntity<ReviewAndCancelStatsResponse> getReviewAndCancelStats(
            @RequestParam Long storeId,
            @RequestParam String dateRange,
            @RequestParam(required = false) Integer ratingMin) {
        ReviewAndCancelStatsResponse stats = dashboardService.getReviewAndCancelStats(storeId, dateRange, ratingMin);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/promotions/events")
    @Operation(summary = "프로모션/이벤트 목록 조회", description = "특정 가게의 모든 프로모션 및 기념일 목록을 조회합니다.")
    // TODO: 로그인한 사용자가 자신의 가게 정보만 볼 수 있도록 보안 로직 추가 필요
    public ResponseEntity<List<PromotionEventResponse>> getPromotionEvents(@RequestParam Long storeId) {
        List<PromotionEventResponse> events = dashboardService.getPromotionEvents(storeId);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/promotions/events")
    @Operation(summary = "프로모션/이벤트 생성", description = "새로운 프로모션 또는 기념일을 등록합니다.")
    @PreAuthorize("hasRole('HOST') or hasRole('ADMIN')") // 자영업자 또는 관리자만 생성 가능
    public ResponseEntity<Void> createPromotionEvent(@Valid @RequestBody CreatePromotionEventRequest request) {
        dashboardService.createPromotionEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

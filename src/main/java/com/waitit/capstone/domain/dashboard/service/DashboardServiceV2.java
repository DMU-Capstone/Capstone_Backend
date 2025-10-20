package com.waitit.capstone.domain.dashboard.service;

import com.waitit.capstone.domain.dashboard.dto.*;
import com.waitit.capstone.domain.dashboard.entity.QueueLog;
import com.waitit.capstone.domain.dashboard.entity.Review;
import com.waitit.capstone.domain.dashboard.repository.QueueLogRepository;
import com.waitit.capstone.domain.dashboard.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceV2 {

    private final QueueLogRepository queueLogRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 1. 전체 스토어 지표 조회
     */
    public StoreMetricsResponse getStoreMetrics(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);

        // 1. 전체 요약 통계 계산
        long totalWaitlistCount = queueLogRepository.countByHostIdAndRegisteredAtBetween(storeId, startDate, endDate);
        long totalActualUsers = queueLogRepository.countByHostIdAndStatusAndEnteredAtBetween(storeId, QueueLog.Status.ENTERED, startDate, endDate);
        long totalDropouts = queueLogRepository.countByHostIdAndStatusAndCancelledAtBetween(storeId, QueueLog.Status.CANCELLED, startDate, endDate);
        double dropoutRate = (totalWaitlistCount > 0) ? (double) totalDropouts / totalWaitlistCount : 0.0;
        Double avgWaitTime = queueLogRepository.findAverageWaitTimeInSeconds(storeId, startDate, endDate);

        StoreMetricsSummaryDto summary = StoreMetricsSummaryDto.builder()
                .totalWaitlistCount(totalWaitlistCount)
                .totalActualUsers(totalActualUsers)
                .totalDropouts(totalDropouts)
                .dropoutRate(dropoutRate)
                .averageWaitTimeSeconds(avgWaitTime != null ? avgWaitTime : 0.0)
                .build();

        // 2. 시간대별 세부 통계 계산
        List<HourlyMetricsQueryResult> hourlyResults = queueLogRepository.findHourlyMetrics(storeId, startDate, endDate);
        List<HourlyStoreMetricsDto> hourlyData = hourlyResults.stream()
                .map(result -> HourlyStoreMetricsDto.builder()
                        .timeSlot(String.format("%02d:00 - %02d:00", result.getHour(), result.getHour() + 1))
                        .waitlistCount(result.getTotalCount())
                        .actualUsers(result.getEnteredCount())
                        .dropouts(result.getCancelledCount())
                        .build())
                .collect(Collectors.toList());

        // 3. 최종 응답 조합
        return StoreMetricsResponse.builder()
                .summary(summary)
                .hourlyData(hourlyData)
                .build();
    }

    /**
     * 4. 예상 대기인원 추이 조회
     */
    public List<WaitlistTrendHourlyData> getWaitlistTrend(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);

        List<HourlyMetricsQueryResult> hourlyResults = queueLogRepository.findHourlyMetrics(storeId, startDate, endDate);

        return hourlyResults.stream()
                .map(result -> {
                    double utilizationRate = (result.getTotalCount() > 0) ? (double) result.getEnteredCount() / result.getTotalCount() : 0.0;
                    return WaitlistTrendHourlyData.builder()
                            .timeSlot(String.format("%02d:00 - %02d:00", result.getHour(), result.getHour() + 1))
                            .waitlistCount(result.getTotalCount())
                            .utilizationRate(utilizationRate)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 3. 재방문율 조회
     */
    public ReturnRateResponse getReturnRate(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);

        Set<String> totalVisitorPhones = queueLogRepository.findDistinctPhoneNumbersByStatusAndEnteredAtBetween(storeId, startDate, endDate);
        long totalVisitors = totalVisitorPhones.size();

        if (totalVisitors == 0) {
            return ReturnRateResponse.builder()
                    .totalVisitors(0).newVisitors(0).returningVisitors(0).returnRate(0.0).build();
        }

        Set<String> returningVisitorPhones = queueLogRepository.findReturningVisitorPhoneNumbers(storeId, totalVisitorPhones, startDate);
        long returningVisitors = returningVisitorPhones.size();

        long newVisitors = totalVisitors - returningVisitors;
        double returnRate = (double) returningVisitors / totalVisitors;

        return ReturnRateResponse.builder()
                .totalVisitors(totalVisitors)
                .newVisitors(newVisitors)
                .returningVisitors(returningVisitors)
                .returnRate(returnRate)
                .build();
    }

    /**
     * 5. 리뷰 및 대기 취소 사유 조회
     */
    public ReviewAndCancelStatsResponse getReviewAndCancelStats(Long storeId, String dateRange, Integer ratingMin) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
        int minRating = (ratingMin != null) ? ratingMin : 0;

        // 1. 취소 사유 통계 계산
        List<CancelReasonCountDto> reasonCounts = queueLogRepository.findCancelReasonStats(storeId, startDate, endDate);
        long totalCancellations = reasonCounts.stream().mapToLong(CancelReasonCountDto::getCount).sum();

        List<CancelReasonStatsDto> cancelReasons = reasonCounts.stream()
                .map(dto -> {
                    double percentage = (totalCancellations > 0) ? (double) dto.getCount() / totalCancellations : 0.0;
                    return CancelReasonStatsDto.builder()
                            .reason(dto.getReason())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());

        // 2. 리뷰 목록 조회
        List<Review> reviewEntities = reviewRepository.findReviews(storeId, startDate, endDate, minRating);
        List<ReviewSummaryDto> reviews = reviewEntities.stream()
                .map(ReviewSummaryDto::from)
                .collect(Collectors.toList());

        return ReviewAndCancelStatsResponse.builder()
                .cancelReasons(cancelReasons)
                .reviews(reviews)
                .build();
    }
}

package com.waitit.capstone.domain.dashboard.service;

import com.waitit.capstone.domain.dashboard.dto.*;
import com.waitit.capstone.domain.dashboard.entity.QueueLog;
import com.waitit.capstone.domain.dashboard.entity.Review;
import com.waitit.capstone.domain.dashboard.repository.QueueLogRepositoryV2;
import com.waitit.capstone.domain.dashboard.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    private final QueueLogRepositoryV2 queueLogRepository;
    private final ReviewRepository reviewRepository;

    /**
     * [통합] 모든 대시보드 데이터를 한번에 조회합니다.
     */
    public IntegratedDashboardResponse getIntegratedDashboardData(Long storeId, String dateRange) {
        return IntegratedDashboardResponse.builder()
                .storeMetrics(getStoreMetrics(storeId, dateRange))
                .peakAnalysis(getPeakAnalysis(storeId, dateRange))
                .returnRate(getReturnRate(storeId, dateRange))
                .waitlistTrend(getWaitlistTrend(storeId, dateRange))
                .reviewAndCancelStats(getReviewAndCancelStats(storeId, dateRange, 0)) // ratingMin 기본값 0으로 조회
                .build();
    }

    public StoreMetricsResponse getStoreMetrics(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);

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

        List<Object[]> hourlyResults = queueLogRepository.findHourlyMetricsRaw(storeId, startDate, endDate);
        List<HourlyStoreMetricsDto> hourlyData = hourlyResults.stream()
                .map(result -> {
                    Integer hour = ((Number) result[0]).intValue();
                    long totalCount = ((Number) result[1]).longValue();
                    long enteredCount = ((Number) result[2]).longValue();
                    long cancelledCount = ((Number) result[3]).longValue();
                    return HourlyStoreMetricsDto.builder()
                            .timeSlot(String.format("%02d:00 - %02d:00", hour, hour + 1))
                            .waitlistCount(totalCount)
                            .actualUsers(enteredCount)
                            .dropouts(cancelledCount)
                            .build();
                })
                .collect(Collectors.toList());

        return StoreMetricsResponse.builder()
                .summary(summary)
                .hourlyData(hourlyData)
                .build();
    }

    public List<WaitlistTrendHourlyData> getWaitlistTrend(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);

        List<Object[]> hourlyResults = queueLogRepository.findHourlyMetricsRaw(storeId, startDate, endDate);

        return hourlyResults.stream()
                .map(result -> {
                    Integer hour = ((Number) result[0]).intValue();
                    long totalCount = ((Number) result[1]).longValue();
                    long enteredCount = ((Number) result[2]).longValue();
                    double utilizationRate = (totalCount > 0) ? (double) enteredCount / totalCount : 0.0;
                    return WaitlistTrendHourlyData.builder()
                            .timeSlot(String.format("%02d:00 - %02d:00", hour, hour + 1))
                            .waitlistCount(totalCount)
                            .utilizationRate(utilizationRate)
                            .build();
                })
                .collect(Collectors.toList());
    }

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

    public ReviewAndCancelStatsResponse getReviewAndCancelStats(Long storeId, String dateRange, Integer ratingMin) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
        int minRating = (ratingMin != null) ? ratingMin : 0;

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

        List<Review> reviewEntities = reviewRepository.findReviews(storeId, startDate, endDate, minRating);
        List<ReviewSummaryDto> reviews = reviewEntities.stream()
                .map(ReviewSummaryDto::from)
                .collect(Collectors.toList());

        return ReviewAndCancelStatsResponse.builder()
                .cancelReasons(cancelReasons)
                .reviews(reviews)
                .build();
    }

    public List<PeakAnalysisDataDto> getPeakAnalysis(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);

        List<Object[]> results = queueLogRepository.findPeakAnalysisStatsRaw(storeId, startDate, endDate);

        return results.stream()
                .map(result -> {
                    Integer hour = ((Number) result[0]).intValue();
                    long totalCount = ((Number) result[1]).longValue();
                    long enteredCount = ((Number) result[2]).longValue();
                    long cancelledCount = ((Number) result[3]).longValue();
                    Double avgWaitTime = (result[4] != null) ? ((BigDecimal) result[4]).doubleValue() : 0.0;

                    double dropoutRate = (totalCount > 0) ? (double) cancelledCount / totalCount : 0.0;

                    return PeakAnalysisDataDto.builder()
                            .timeSlot(String.format("%02d:00 - %02d:00", hour, hour + 1))
                            .customersServed(enteredCount)
                            .averageProcessingSpeedSeconds(avgWaitTime)
                            .dropoutRate(dropoutRate)
                            .build();
                })
                .collect(Collectors.toList());
    }
}

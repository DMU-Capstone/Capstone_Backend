package com.waitit.capstone.domain.dashboard.service;

import com.waitit.capstone.domain.dashboard.dto.*;
import com.waitit.capstone.domain.dashboard.entity.PromotionEvent;
import com.waitit.capstone.domain.dashboard.entity.Review;
import com.waitit.capstone.domain.dashboard.repository.*;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final PromotionEventRepository promotionEventRepository;
    private final VisitHistoryRepository visitHistoryRepository;
    private final WaitingHistoryRepository waitingHistoryRepository;
    private final QueueCancellationRepository queueCancellationRepository;
    private final ReviewRepository reviewRepository; // 리포지토리 주입
    private final HostRepository hostRepository;

    // ... (기존 메소드 생략)

    /**
     * 리뷰 및 대기 취소 사유 통계를 조회합니다.
     */
    public ReviewAndCancelStatsResponse getReviewAndCancelStats(Long storeId, String dateRange, Integer ratingMin) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
        int minRating = (ratingMin != null) ? ratingMin : 0;

        // 1. 취소 사유 통계 조회
        List<CancelReasonStatsDto> cancelReasons = queueCancellationRepository.findCancelReasonStats(storeId, startDate, endDate);

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
    
    public ReportSummaryResponse getReportSummary(Long storeId, String type) {
        List<ReportSummaryData> data = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 3; i >= 0; i--) {
            LocalDate periodEnd = (type.equals("WEEKLY")) ? today.minusWeeks(i) : today.minusMonths(i);
            LocalDate periodStart = (type.equals("WEEKLY")) ? periodEnd.minusWeeks(1).plusDays(1) : periodEnd.with(TemporalAdjusters.firstDayOfMonth());

            String label = (i == 0) ? "이번주" : (i == 1) ? "지난주" : i + "주전";
            if (type.equals("MONTHLY")) {
                label = (i == 0) ? "이번달" : i + "개월전";
            }

            ReportSummaryData periodData = calculatePeriodStats(storeId, periodStart.atStartOfDay(), periodEnd.atTime(LocalTime.MAX));
            data.add(ReportSummaryData.builder()
                    .label(label)
                    .visitors(periodData.getVisitors())
                    .dropRate(periodData.getDropRate())
                    .returnRate(periodData.getReturnRate())
                    .build());
        }

        return ReportSummaryResponse.builder()
                .periodType(type)
                .data(data)
                .build();
    }

    private ReportSummaryData calculatePeriodStats(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        Set<String> totalVisitorPhones = visitHistoryRepository.findDistinctPhoneNumbersByHostIdAndVisitedAtBetween(storeId, startDate, endDate);
        long totalVisitors = totalVisitorPhones.size();

        double returnRate = 0.0;
        if (totalVisitors > 0) {
            Set<String> returningVisitorPhones = visitHistoryRepository.findReturningVisitorPhoneNumbers(storeId, totalVisitorPhones, startDate);
            returnRate = (double) returningVisitorPhones.size() / totalVisitors;
        }

        long cancellationCount = queueCancellationRepository.countByHostIdAndCancelledAtBetween(storeId, startDate, endDate);
        long totalEntries = totalVisitors + cancellationCount;
        double dropRate = (totalEntries > 0) ? (double) cancellationCount / totalEntries : 0.0;

        return ReportSummaryData.builder()
                .visitors(totalVisitors)
                .returnRate(returnRate)
                .dropRate(dropRate)
                .build();
    }
    
    public WaitingTrendsResponse getWaitingTrends(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);
        List<HourlyWaitingStats> actualStats = waitingHistoryRepository.findHourlyAverageQueueSize(storeId, startDate, endDate);
        LocalDateTime predictedStartDate = startDate.minusWeeks(4);
        List<HourlyWaitingStats> predictedStats = waitingHistoryRepository.findHourlyAverageQueueSize(storeId, predictedStartDate, startDate.minusDays(1));
        int startHour = 9;
        int endHour = 22;
        List<String> labels = IntStream.rangeClosed(startHour, endHour).mapToObj(h -> h + "시").collect(Collectors.toList());
        Map<Integer, Double> actualMap = actualStats.stream().collect(Collectors.toMap(HourlyWaitingStats::getHour, HourlyWaitingStats::getAverageQueueSize));
        Map<Integer, Double> predictedMap = predictedStats.stream().collect(Collectors.toMap(HourlyWaitingStats::getHour, HourlyWaitingStats::getAverageQueueSize));
        List<Double> actualData = IntStream.rangeClosed(startHour, endHour).mapToDouble(h -> actualMap.getOrDefault(h, 0.0)).boxed().collect(Collectors.toList());
        List<Double> predictedData = IntStream.rangeClosed(startHour, endHour).mapToDouble(h -> predictedMap.getOrDefault(h, 0.0)).boxed().collect(Collectors.toList());
        return WaitingTrendsResponse.builder()
                .labels(labels)
                .actual(actualData)
                .predicted(predictedData)
                .build();
    }
    
    public VisitorStatisticsResponse getVisitorStatistics(Long storeId, String dateRange) {
        String[] dates = dateRange.split("~");
        LocalDateTime startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.MAX);

        Set<String> totalVisitorPhones = visitHistoryRepository.findDistinctPhoneNumbersByHostIdAndVisitedAtBetween(storeId, startDate, endDate);
        long totalVisitors = totalVisitorPhones.size();

        if (totalVisitors == 0) {
            return VisitorStatisticsResponse.builder().build();
        }

        Set<String> returningVisitorPhones = visitHistoryRepository.findReturningVisitorPhoneNumbers(storeId, totalVisitorPhones, startDate);
        long returningVisitors = returningVisitorPhones.size();

        long newVisitors = totalVisitors - returningVisitors;
        double returnRate = (double) returningVisitors / totalVisitors;

        return VisitorStatisticsResponse.builder()
                .totalVisitors(totalVisitors)
                .newVisitors(newVisitors)
                .returningVisitors(returningVisitors)
                .returnRate(returnRate)
                .build();
    }
    
    public List<PromotionEventResponse> getPromotionEvents(Long storeId) {
        List<PromotionEvent> events = promotionEventRepository.findByHostId(storeId);
        return events.stream()
                .map(PromotionEventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void createPromotionEvent(CreatePromotionEventRequest request) {
        Host host = hostRepository.findById(request.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 가게를 찾을 수 없습니다: " + request.getStoreId()));

        PromotionEvent event = PromotionEvent.builder()
                .host(host)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        promotionEventRepository.save(event);
    }
}

package com.waitit.capstone.domain.dashboard.repository;

import com.waitit.capstone.domain.dashboard.dto.HourlyWaitingStats;
import com.waitit.capstone.domain.dashboard.entity.WaitingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WaitingHistoryRepository extends JpaRepository<WaitingHistory, Long> {

    /**
     * 특정 기간 동안의 시간대별 평균 대기 인원을 계산합니다.
     */
    @Query("SELECT new com.waitit.capstone.domain.dashboard.dto.HourlyWaitingStats(FUNCTION('HOUR', w.recordedAt), AVG(w.queueSize)) " +
           "FROM WaitingHistory w " +
           "WHERE w.host.id = :hostId AND w.recordedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('HOUR', w.recordedAt) " +
           "ORDER BY FUNCTION('HOUR', w.recordedAt)")
    List<HourlyWaitingStats> findHourlyAverageQueueSize(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

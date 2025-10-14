package com.waitit.capstone.domain.dashboard.repository;

import com.waitit.capstone.domain.dashboard.dto.CancelReasonStatsDto;
import com.waitit.capstone.domain.dashboard.entity.QueueCancellation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QueueCancellationRepository extends JpaRepository<QueueCancellation, Long> {

    /**
     * 특정 기간 동안 특정 가게의 대기 취소 건수를 계산합니다.
     */
    long countByHostIdAndCancelledAtBetween(Long hostId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 기간 동안 특정 가게의 대기 취소 사유별 통계를 조회합니다.
     */
    @Query("SELECT new com.waitit.capstone.domain.dashboard.dto.CancelReasonStatsDto(q.reason, COUNT(q)) " +
           "FROM QueueCancellation q " +
           "WHERE q.host.id = :hostId AND q.cancelledAt BETWEEN :startDate AND :endDate " +
           "GROUP BY q.reason")
    List<CancelReasonStatsDto> findCancelReasonStats(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

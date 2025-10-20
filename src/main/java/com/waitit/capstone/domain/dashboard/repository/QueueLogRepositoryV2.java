package com.waitit.capstone.domain.dashboard.repository;

import com.waitit.capstone.domain.dashboard.dto.CancelReasonCountDto;
import com.waitit.capstone.domain.dashboard.entity.QueueLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

// 인터페이스 이름을 V2로 변경
public interface QueueLogRepositoryV2 extends JpaRepository<QueueLog, Long> {

    long countByHostIdAndRegisteredAtBetween(Long hostId, LocalDateTime startDate, LocalDateTime endDate);

    long countByHostIdAndStatusAndEnteredAtBetween(Long hostId, QueueLog.Status status, LocalDateTime startDate, LocalDateTime endDate);

    long countByHostIdAndStatusAndCancelledAtBetween(Long hostId, QueueLog.Status status, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT AVG(TIMESTAMPDIFF(SECOND, q.registered_at, q.entered_at)) FROM queue_log q " +
                   "WHERE q.host_id = :hostId AND q.status = 'ENTERED' AND q.entered_at BETWEEN :startDate AND :endDate",
           nativeQuery = true)
    Double findAverageWaitTimeInSeconds(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT " +
                   "    HOUR(q.registered_at) as hour, " +
                   "    COUNT(q.id) as totalCount, " +
                   "    SUM(CASE WHEN q.status = 'ENTERED' THEN 1 ELSE 0 END) as enteredCount, " +
                   "    SUM(CASE WHEN q.status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelledCount " +
                   "FROM queue_log q " +
                   "WHERE q.host_id = :hostId AND q.registered_at BETWEEN :startDate AND :endDate " +
                   "GROUP BY hour ORDER BY hour",
           nativeQuery = true)
    List<Object[]> findHourlyMetricsRaw(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT q.phoneNumber FROM QueueLog q " +
           "WHERE q.host.id = :hostId AND q.status = 'ENTERED' AND q.enteredAt BETWEEN :startDate AND :endDate")
    Set<String> findDistinctPhoneNumbersByStatusAndEnteredAtBetween(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DISTINCT q.phoneNumber FROM QueueLog q " +
           "WHERE q.host.id = :hostId AND q.status = 'ENTERED' AND q.phoneNumber IN :phoneNumbers AND q.enteredAt < :startDate")
    Set<String> findReturningVisitorPhoneNumbers(
            @Param("hostId") Long hostId,
            @Param("phoneNumbers") Set<String> phoneNumbers,
            @Param("startDate") LocalDateTime startDate);

    @Query("SELECT new com.waitit.capstone.domain.dashboard.dto.CancelReasonCountDto(q.reason, COUNT(q)) " +
           "FROM QueueLog q " +
           "WHERE q.host.id = :hostId AND q.status = 'CANCELLED' AND q.cancelledAt BETWEEN :startDate AND :endDate " +
           "GROUP BY q.reason")
    List<CancelReasonCountDto> findCancelReasonStats(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}

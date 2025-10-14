package com.waitit.capstone.domain.dashboard.repository;

import com.waitit.capstone.domain.dashboard.entity.VisitHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface VisitHistoryRepository extends JpaRepository<VisitHistory, Long> {

    /**
     * 특정 기간 동안 특정 가게를 방문한 모든 고객의 전화번호를 중복 없이 조회합니다.
     */
    @Query("SELECT DISTINCT v.phoneNumber FROM VisitHistory v WHERE v.host.id = :hostId AND v.visitedAt BETWEEN :startDate AND :endDate")
    Set<String> findDistinctPhoneNumbersByHostIdAndVisitedAtBetween(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * 주어진 전화번호 목록 중, 특정 시점 이전에 방문한 기록이 있는 전화번호 목록을 조회합니다.
     */
    @Query("SELECT DISTINCT v.phoneNumber FROM VisitHistory v WHERE v.host.id = :hostId AND v.phoneNumber IN :phoneNumbers AND v.visitedAt < :startDate")
    Set<String> findReturningVisitorPhoneNumbers(
            @Param("hostId") Long hostId,
            @Param("phoneNumbers") Set<String> phoneNumbers,
            @Param("startDate") LocalDateTime startDate);

}

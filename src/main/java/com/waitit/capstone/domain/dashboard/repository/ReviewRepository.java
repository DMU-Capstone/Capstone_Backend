package com.waitit.capstone.domain.dashboard.repository;

import com.waitit.capstone.domain.dashboard.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 특정 가게의 리뷰를 기간, 최소 평점 기준으로 필터링하여 최신순으로 조회합니다.
     */
    @Query("SELECT r FROM Review r WHERE r.host.id = :hostId " +
           "AND r.createdAt BETWEEN :startDate AND :endDate " +
           "AND r.rating >= :ratingMin " +
           "ORDER BY r.createdAt DESC")
    List<Review> findReviews(
            @Param("hostId") Long hostId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("ratingMin") int ratingMin);

}

package com.waitit.capstone.domain.dashboard.repository;

import com.waitit.capstone.domain.dashboard.entity.PromotionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionEventRepository extends JpaRepository<PromotionEvent, Long> {
    // 특정 가게(Host)에 속한 모든 이벤트를 조회하기 위한 메소드
    List<PromotionEvent> findByHostId(Long hostId);
}

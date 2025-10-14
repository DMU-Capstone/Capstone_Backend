package com.waitit.capstone.domain.dashboard.dto;

import lombok.Getter;

@Getter
public class HourlyWaitingStats {
    private Integer hour;
    private Double averageQueueSize;

    // JPQL의 new 생성자 표현식을 위해 명시적으로 생성자 추가
    public HourlyWaitingStats(Integer hour, Double averageQueueSize) {
        this.hour = hour;
        this.averageQueueSize = averageQueueSize;
    }
}

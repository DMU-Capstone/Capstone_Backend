package com.waitit.capstone.domain.dashboard.dto;

import lombok.Getter;

@Getter
public class HourlyWaitingStats {
    private Integer hour;
    private Double averageQueueSize;

    public HourlyWaitingStats(Number hour, Number averageQueueSize) {
        this.hour = hour.intValue();
        this.averageQueueSize = averageQueueSize.doubleValue();
    }
}
package com.waitit.capstone.domain.dashboard.dto;

import lombok.Getter;

@Getter
public class HourlyMetricsQueryResult {
    private final Long hour;
    private final Long totalCount;
    private final Long enteredCount;
    private final Long cancelledCount;


    public HourlyMetricsQueryResult(Integer hour, Long totalCount, Long enteredCount, Long cancelledCount) {
        this.hour = hour == null ? null : hour.longValue();
        this.totalCount = totalCount;
        this.enteredCount = enteredCount;
        this.cancelledCount = cancelledCount;
    }
}
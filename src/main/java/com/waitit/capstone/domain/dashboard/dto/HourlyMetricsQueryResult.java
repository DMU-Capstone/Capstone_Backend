package com.waitit.capstone.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyMetricsQueryResult {
    private int hour;
    private long totalCount;
    private long enteredCount;
    private long cancelledCount;
}

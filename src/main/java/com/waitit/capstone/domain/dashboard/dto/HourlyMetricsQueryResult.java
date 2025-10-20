package com.waitit.capstone.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyMetricsQueryResult {
    private Integer hour;
    private Long totalCount;
    private Long enteredCount;
    private Long cancelledCount;
}

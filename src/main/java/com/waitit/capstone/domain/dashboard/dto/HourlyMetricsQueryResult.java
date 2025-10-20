package com.waitit.capstone.domain.dashboard.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyMetricsQueryResult {
    private final Long hour;
    private final Long totalCount;
    private final Long enteredCount;
    private final Long cancelledCount;

}

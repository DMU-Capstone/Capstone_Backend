package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VisitorStatisticsResponse {
    private long totalVisitors;
    private long newVisitors;
    private long returningVisitors;
    private double returnRate;
}

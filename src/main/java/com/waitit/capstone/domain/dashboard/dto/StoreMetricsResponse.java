package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreMetricsResponse {
    private StoreMetricsSummaryDto summary;
    private List<HourlyStoreMetricsDto> hourlyData;
}

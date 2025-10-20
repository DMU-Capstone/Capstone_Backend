package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HourlyStoreMetricsDto {
    private String timeSlot; // 예: "10:00 - 11:00"
    private long waitlistCount;
    private long actualUsers;
    private long dropouts;
}

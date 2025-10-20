package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WaitlistTrendHourlyData {
    private String timeSlot;        // 시간대 (예: "10:00 - 11:00")
    private long waitlistCount;     // 해당 시간대 대기열 등록 수
    private double utilizationRate; // 실제 이용률 (입장 수 / 등록 수)
}

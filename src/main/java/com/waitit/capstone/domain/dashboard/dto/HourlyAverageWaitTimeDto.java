package com.waitit.capstone.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyAverageWaitTimeDto {
    private int hour;
    private double averageWaitTime;
}

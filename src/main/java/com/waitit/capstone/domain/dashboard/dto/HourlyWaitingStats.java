package com.waitit.capstone.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyWaitingStats {
    private Integer hour;
    private Double averageQueueSize;
}

package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportSummaryData {
    private String label;
    private long visitors;
    private double dropRate;
    private double returnRate;
}

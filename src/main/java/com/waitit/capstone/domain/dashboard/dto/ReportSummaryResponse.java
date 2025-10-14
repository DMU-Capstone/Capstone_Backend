package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReportSummaryResponse {
    private String periodType;
    private List<ReportSummaryData> data;
}

package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WaitingTrendsResponse {
    private List<String> labels;
    private List<Double> actual;
    private List<Double> predicted;
}

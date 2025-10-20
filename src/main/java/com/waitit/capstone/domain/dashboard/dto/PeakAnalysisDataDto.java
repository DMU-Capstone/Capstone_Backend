package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PeakAnalysisDataDto {
    private String timeSlot;                      // 시간대 (예: "13:00 - 14:00")
    private long customersServed;               // 수용 효율 (시간당 실제 입장 고객 수)
    private double averageProcessingSpeedSeconds; // 평균 처리 속도 (초)
    private double dropoutRate;                   // 서비스 변동률 (시간당 이탈률)
}

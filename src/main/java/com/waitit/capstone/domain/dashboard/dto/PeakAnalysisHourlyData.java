package com.waitit.capstone.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PeakAnalysisHourlyData {
    private String timeSlot; // 예: "10:00 - 11:00"
    private double capacityUtilization; // 자원 대비 수용 효율 (입장수 / max_people)
    private double averageProcessingSpeedSeconds; // 평균 처리 속도 (평균 대기 시간)
    private double serviceFluctuationRate; // 서비스 변동률 (이탈률)
}

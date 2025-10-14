package com.waitit.capstone.domain.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreatePromotionEventRequest {

    @NotNull(message = "가게 ID는 필수입니다.")
    private Long storeId;

    @NotBlank(message = "이벤트 제목은 필수입니다.")
    private String title;

    @NotNull(message = "시작일은 필수입니다.")
    private LocalDate startDate;

    private LocalDate endDate; // 종료일은 선택사항
}

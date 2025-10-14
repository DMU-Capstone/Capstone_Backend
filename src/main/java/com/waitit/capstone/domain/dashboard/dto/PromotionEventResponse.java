package com.waitit.capstone.domain.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.waitit.capstone.domain.dashboard.entity.PromotionEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // end 필드가 null일 경우 JSON에서 제외
public class PromotionEventResponse {

    private String start;
    private String end;
    private String title;

    public static PromotionEventResponse from(PromotionEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // yyyy-MM-dd 형식
        return PromotionEventResponse.builder()
                .title(event.getTitle())
                .start(event.getStartDate().format(formatter))
                .end(event.getEndDate() != null ? event.getEndDate().format(formatter) : null)
                .build();
    }
}

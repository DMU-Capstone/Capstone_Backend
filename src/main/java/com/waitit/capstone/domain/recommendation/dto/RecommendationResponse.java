package com.waitit.capstone.domain.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendationResponse {
    private boolean isWaiting;
    private Object recommendations;

    public static RecommendationResponse of(boolean isWaiting, Object recommendations) {
        return RecommendationResponse.builder()
                .isWaiting(isWaiting)
                .recommendations(recommendations)
                .build();
    }
}

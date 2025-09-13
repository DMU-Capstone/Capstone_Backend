package com.waitit.capstone.domain.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.waitit.capstone.domain.kakao.dto.KakaoSearchResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // 값이 null인 필드는 응답 JSON에서 제외
public class RecommendationResponse {

    private boolean isWaiting;
    private KakaoSearchResponse placeRecommendations; // 사용자가 대기 중일 때 사용
    private List<NearbyHostResponse> hostRecommendations; // 사용자가 대기 중이 아닐 때 사용

}

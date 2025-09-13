package com.waitit.capstone.domain.recommendation.controller;

import com.waitit.capstone.domain.recommendation.dto.RecommendationResponse;
import com.waitit.capstone.domain.recommendation.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recommendations")
@Tag(name = "장소 추천 API", description = "사용자 상황에 맞는 주변 장소를 추천하는 API")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    @Operation(summary = "주변 장소 추천", description = "사용자의 대기 상태와 위치에 따라 주변 장소를 추천합니다. 대기 중이면 대기 시간에 맞는 장소를, 대기 중이 아니면 주변 대기열이 있는 가게를 추천합니다.")
    public ResponseEntity<RecommendationResponse> getRecommendations(
            @RequestParam String phoneNumber,
            @RequestParam double latitude,
            @RequestParam double longitude) {

        RecommendationResponse result = recommendationService.recommend(phoneNumber, latitude, longitude);
        return ResponseEntity.ok(result);
    }
}

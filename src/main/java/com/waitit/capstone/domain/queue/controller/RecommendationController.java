package com.waitit.capstone.domain.queue.controller;

import com.waitit.capstone.domain.manager.dto.CoordinateDto;
import com.waitit.capstone.domain.queue.dto.PlaceResponseDto;
import com.waitit.capstone.domain.queue.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "장소 추천 API", description = "사용자 위치와 대기 시간 기반 장소 추천 관련 API")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Operation(summary = "주변 장소 추천", description = "사용자의 현재 위치와 예상 대기 시간을 기반으로 주변 장소를 추천합니다.")
    @GetMapping("/recommendations")
    public ResponseEntity<PlaceResponseDto> getRecommendations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam int waitingTime) {
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.recommendPlaces(new CoordinateDto(latitude, longitude), waitingTime));
    }
}

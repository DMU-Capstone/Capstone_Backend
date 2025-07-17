package com.waitit.capstone.domain.queue.controller;

import com.waitit.capstone.domain.manager.dto.CoordinateDto;
import com.waitit.capstone.domain.queue.dto.PlaceResponseDto;
import com.waitit.capstone.domain.queue.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping("/recommendations")
    public ResponseEntity<PlaceResponseDto> getRecommendations(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam int waitingTime) {
        return ResponseEntity.status(HttpStatus.OK).body(recommendationService.recommendPlaces(new CoordinateDto(latitude, longitude), waitingTime));
    }
}

package com.waitit.capstone.domain.queue.controller;

import com.waitit.capstone.domain.queue.service.RecommendationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
}

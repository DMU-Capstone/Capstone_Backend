package com.waitit.capstone.domain.queue.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlaceResponseDto {
    private final List<PlaceDto> recommendedPlaces;
}

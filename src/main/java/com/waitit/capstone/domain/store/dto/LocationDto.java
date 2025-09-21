package com.waitit.capstone.domain.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationDto {
    private String address;
    private String station;
    private String distance;
    private Double latitude;
    private Double longitude;
}

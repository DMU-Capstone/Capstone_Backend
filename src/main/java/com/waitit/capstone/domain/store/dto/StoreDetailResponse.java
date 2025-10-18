package com.waitit.capstone.domain.store.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class StoreDetailResponse {
    private Long id;
    private String name;
    private String description;
    private String keyword; // keyword 필드 추가
    private List<String> images;
    private LocationDto location;
    private OperatingHoursDto operating_hours;
}

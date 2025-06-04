package com.waitit.capstone.domain.admin.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AllHostRequest {

    private Long id;

    private String hostName;

    private Integer maxPeople;

    private String hostManagerName;

    private String hostPhoneNumber;

    private Double latitude;
    private Double longitude;

    private String keyword;

    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isActive;
}

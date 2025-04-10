package com.waitit.capstone.domain.client.manager.dto;

import com.waitit.capstone.domain.client.manager.Host;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class HostRequest {
    private String imgUrl;
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

    public Host toEntity() {
        return Host.builder()
                .imgUrl(imgUrl)
                .hostName(hostName)
                .maxPeople(maxPeople)
                .hostManagerName(hostManagerName)
                .hostPhoneNumber(hostPhoneNumber)
                .latitude(latitude)
                .longitude(longitude)
                .keyword(keyword)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}

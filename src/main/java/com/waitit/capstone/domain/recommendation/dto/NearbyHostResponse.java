package com.waitit.capstone.domain.recommendation.dto;

import com.waitit.capstone.domain.manager.Host;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NearbyHostResponse {
    private Long hostId;
    private String hostName;
    private String description;
    private double latitude;
    private double longitude;
    private int distance; // 미터 단위
    private int waitingCount;

    public static NearbyHostResponse from(Host host, int waitingCount, double distance) {
        return NearbyHostResponse.builder()
                .hostId(host.getId())
                .hostName(host.getHostName())
                .description(host.getDescription())
                .latitude(host.getLatitude())
                .longitude(host.getLongitude())
                .distance((int) distance)
                .waitingCount(waitingCount)
                .build();
    }
}

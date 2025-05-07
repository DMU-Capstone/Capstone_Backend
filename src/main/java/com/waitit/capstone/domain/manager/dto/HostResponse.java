package com.waitit.capstone.domain.manager.dto;

import java.time.LocalDateTime;

public record HostResponse(
        String imgUrl,
        String hostName,
        Integer maxPeople,
        String hostManagerName,
        String hostPhoneNumber,
        Double latitude,
        Double longitude,
        String keyword,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime) {
}

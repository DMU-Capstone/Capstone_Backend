package com.waitit.capstone.domain.client.manager.dto;

import com.waitit.capstone.domain.client.manager.Host;
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

package com.waitit.capstone.domain.admin.dto;

import com.waitit.capstone.domain.image.entity.HostImage;

public record HostSummaryDto(Long id, String name, HostImage hostImage) {}

package com.waitit.capstone.domain.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperatingHoursDto {
    private String open;
    private String close;
}

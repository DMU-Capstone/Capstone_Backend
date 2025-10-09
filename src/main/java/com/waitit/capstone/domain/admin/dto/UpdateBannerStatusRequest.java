package com.waitit.capstone.domain.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBannerStatusRequest {
    private Long imgId;
    private boolean active; // true: ON, false: OFF
}

package com.waitit.capstone.domain.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStoreRequest {
    private Long id;
    private String imgUrl;
    private String title;
}

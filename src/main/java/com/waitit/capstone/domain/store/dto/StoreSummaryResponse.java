package com.waitit.capstone.domain.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreSummaryResponse {
    private Long id;
    private String imgUrl;
    private String title;
}

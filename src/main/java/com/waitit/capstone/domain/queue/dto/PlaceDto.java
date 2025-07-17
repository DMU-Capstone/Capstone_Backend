package com.waitit.capstone.domain.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlaceDto {
    private String placeName;
    private String distance;
    private String placeUrl;
    private String categoryName;
    private String addressName;
    private String roadAddressName;
    private String longitude; // x
    private String latitude;  // y
}

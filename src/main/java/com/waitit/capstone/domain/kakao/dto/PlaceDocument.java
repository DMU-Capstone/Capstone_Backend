package com.waitit.capstone.domain.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder // 추가
@NoArgsConstructor
@AllArgsConstructor // 추가
public class PlaceDocument {

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("distance")
    private String distance;

    @JsonProperty("place_url")
    private String placeUrl;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("road_address_name")
    private String roadAddressName;

    @JsonProperty("x")
    private String longitude;

    @JsonProperty("y")
    private String latitude;
}

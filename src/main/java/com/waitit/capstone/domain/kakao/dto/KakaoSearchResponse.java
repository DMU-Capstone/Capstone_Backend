package com.waitit.capstone.domain.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor // 추가
public class KakaoSearchResponse {

    @JsonProperty("documents")
    private List<PlaceDocument> documents;
}

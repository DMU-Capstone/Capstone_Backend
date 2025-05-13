package com.waitit.capstone.domain.admin.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class MainBannerResponse {
    private String banner;
    private List<String> imgList;
}

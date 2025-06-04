package com.waitit.capstone.domain.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitingListDto {
    private String phoneNumber;
    private String name;
    private int count;
}

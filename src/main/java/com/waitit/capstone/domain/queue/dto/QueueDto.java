package com.waitit.capstone.domain.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueDto {
    private String phoneNumber;
    private String name;
    private int count;
}

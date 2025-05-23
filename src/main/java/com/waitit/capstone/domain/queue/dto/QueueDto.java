package com.waitit.capstone.domain.queue.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueDto {
    private String PhoneNumber;
    private String name;
    private int count;
}

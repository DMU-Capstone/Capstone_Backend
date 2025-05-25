package com.waitit.capstone.domain.queue.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueRequest {
    private String phoneNumber;
    private String name;
    private int count;
}

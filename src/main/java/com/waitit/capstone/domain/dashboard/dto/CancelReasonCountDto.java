package com.waitit.capstone.domain.dashboard.dto;

import com.waitit.capstone.domain.dashboard.entity.QueueLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelReasonCountDto {
    private QueueLog.Reason reason;
    private long count;
}

package com.waitit.capstone.domain.client.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record SessionListDto(
    Long hostId,
    String hostName,
    String imgUrl,
    String estimatedTime,  // 예상 시간 (시작-종료 시간으로부터 계산)
    int waitingCount){}      // 대기 인원 수

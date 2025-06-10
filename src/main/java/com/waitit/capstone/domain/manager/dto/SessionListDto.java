package com.waitit.capstone.domain.manager.dto;


import lombok.Builder;


@Builder
public record SessionListDto(
    Long hostId,
    String hostName,
    String imgUrl,
    String estimatedTime,  // 예상 시간 (시작-종료 시간으로부터 계산)
    int waitingCount){}      // 대기 인원 수

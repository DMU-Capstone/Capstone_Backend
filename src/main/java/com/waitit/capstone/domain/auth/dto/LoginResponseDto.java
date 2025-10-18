package com.waitit.capstone.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // null인 필드는 JSON 응답에서 제외
public class LoginResponseDto {
    private String message;
    private String phoneNumber;
    private String name;
    private String role;
    private String refresh; // 모바일 클라이언트용
}

package com.waitit.capstone.domain.message.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SmsVerifyRequest {
    @NotNull(message = "휴대폰 번호를 입력해주세요.")
    private String phoneNum;
    @NotNull(message = "인증번호를 입력해주세요.")
    private String certificationCode;
}

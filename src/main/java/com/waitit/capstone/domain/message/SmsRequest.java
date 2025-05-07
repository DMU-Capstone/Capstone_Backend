package com.waitit.capstone.domain.message;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest{
    @NotEmpty(message = "휴대폰 번호를 입력해주세요")
    private String phoneNum;
}

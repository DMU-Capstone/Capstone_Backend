package com.waitit.capstone.domain.auth.dto;

import com.waitit.capstone.domain.member.Entity.Gender;
import com.waitit.capstone.domain.member.Entity.Role; // Role 임포트 추가
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequest {

    @NotBlank(message = "이름을 입력해야 합니다.")
    @Pattern(regexp = "^[가-힣]+$", message = "이름에는 한글만 가능합니다")
    private String name;

    @NotBlank(message = "닉네임을 입력해야 합니다.")
    private String nickName;

    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    private String password;

    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
            message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
    private String phoneNumber;

    private Gender gender;

    @NotNull(message = "사용자 역할을 선택해야 합니다.")
    private Role role; // role 필드 추가

}

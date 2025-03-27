package com.dumbbelling.capstone.domain.client.auth.dto;

import com.dumbbelling.capstone.domain.client.member.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDto {
    private String name;
    private String nickname;
    private String password;
    private String phoneNumber;
    private Gender gender;
}

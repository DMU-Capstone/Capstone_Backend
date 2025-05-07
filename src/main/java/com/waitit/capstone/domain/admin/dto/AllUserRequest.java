package com.waitit.capstone.domain.admin.dto;


import com.waitit.capstone.domain.member.Entity.Gender;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class AllUserRequest {
    private Long id;
    private String name;
    private String nickName;
    private String phoneNumber;
    private Gender gender;
}

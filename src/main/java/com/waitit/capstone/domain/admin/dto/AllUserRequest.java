package com.waitit.capstone.domain.admin.dto;


import com.waitit.capstone.domain.client.member.Entity.Gender;
import com.waitit.capstone.domain.client.member.Entity.Member;
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

    public static AllUserRequest from(Member member) {
        return AllUserRequest.builder()
                .id(member.getId())
                .name(member.getName())
                .nickName(member.getNickname())
                .phoneNumber(member.getPhoneNumber())
                .gender(member.getGender())
                .build();
    }
}

package com.waitit.capstone.domain.admin.dto;


import com.waitit.capstone.domain.client.member.Entity.Gender;
import com.waitit.capstone.domain.client.member.Entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AllUserRequest {

    private String name;

    private String nickName;

    private String password;

    private String phoneNumber;

    private Gender gender;

    public AllUserRequest(Member member) {
        this.gender = Gender.MALE;
        this.name = name;
        this.nickName = nickName;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}

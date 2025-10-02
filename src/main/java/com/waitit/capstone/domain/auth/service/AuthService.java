package com.waitit.capstone.domain.auth.service;

import com.waitit.capstone.domain.auth.dto.JoinRequest;
import com.waitit.capstone.domain.member.Entity.Member;
import com.waitit.capstone.domain.member.MemberRepository;
import com.waitit.capstone.domain.member.Entity.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    //회원가입
    public void join(JoinRequest joinRequest) {
        String nickname = joinRequest.getNickName();

        if(memberRepository.existsByNickname(nickname)){
            return;
        }
        Member member = Member.builder()
                .name(joinRequest.getName())
                .nickname(joinRequest.getNickName())
                .password(bCryptPasswordEncoder.encode(joinRequest.getPassword()))
                .phoneNumber(joinRequest.getPhoneNumber())
                .gender(joinRequest.getGender())
                .role(joinRequest.getRole()) // Role.USER -> joinRequest.getRole()로 수정
                .build();

        memberRepository.save(member);
    }
    //휴대폰 번호 인증
}

package com.waitit.capstone.domain.client.auth.service;

import com.waitit.capstone.domain.client.auth.dto.JoinRequest;
import com.waitit.capstone.domain.client.member.Member;
import com.waitit.capstone.domain.client.member.MemberRepository;
import com.waitit.capstone.domain.client.member.Role;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
                .role(Role.USER)
                .build();
        memberRepository.save(member);
    }
}

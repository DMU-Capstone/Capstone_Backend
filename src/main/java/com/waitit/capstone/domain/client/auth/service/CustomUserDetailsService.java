package com.waitit.capstone.domain.client.auth.service;

import com.waitit.capstone.domain.client.auth.dto.CustomUserDetails;
import com.waitit.capstone.domain.client.member.Entity.Member;
import com.waitit.capstone.domain.client.member.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByPhoneNumber(username);
        if(member!=null){
            return new CustomUserDetails(member);
        }
        return null;
    }
}

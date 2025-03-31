package com.dumbbelling.capstone.domain.client.auth;

import com.dumbbelling.capstone.domain.client.auth.dto.CustomUserDetails;
import com.dumbbelling.capstone.domain.client.member.Member;
import com.dumbbelling.capstone.domain.client.member.MemberRepository;
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

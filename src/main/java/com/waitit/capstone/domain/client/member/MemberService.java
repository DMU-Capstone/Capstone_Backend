package com.waitit.capstone.domain.client.member;

import com.waitit.capstone.domain.admin.dto.UpdatedRequest;
import com.waitit.capstone.domain.client.auth.dto.CustomUserDetails;
import com.waitit.capstone.domain.client.member.Entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    public Member updateMember(UpdatedRequest updatedRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = customUserDetails.getMember();
        member.updateProfile(updatedRequest.getName(), updatedRequest.getNickName(), updatedRequest.getPassword());

        return memberRepository.save(member);
    }
}

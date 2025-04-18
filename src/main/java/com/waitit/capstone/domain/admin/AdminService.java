package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.admin.dto.UpdatedRequest;
import com.waitit.capstone.domain.client.auth.dto.CustomUserDetails;
import com.waitit.capstone.domain.client.member.Entity.Member;
import com.waitit.capstone.domain.client.member.MemberRepository;
import com.waitit.capstone.global.util.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class AdminService {

    private final MemberRepository memberRepository;

    //모든 유저를 조회후 페이징
    public PageResponse<AllUserRequest> getAllUser(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        Page<AllUserRequest> allUserRequests = members.map(AllUserRequest::from);

        return new PageResponse<>(allUserRequests);
    }

    //유저 리퀘스트 바디를 받아서 수정후 저장
    public void updateMember(UpdatedRequest request) {
        Long memberId = Long.parseLong(request.getId());
        Member member = memberRepository.findMemberById(memberId);

        member.updateProfile(request.getName(), request.getName(), request.getPassword());
        memberRepository.save(member);
    }

    //아이디로 삭제
    public void deleteMember(Long id) {
        memberRepository.deleteById(id);
    }

    //이벤트 배너 등록

    //대기열 현황 조회
}

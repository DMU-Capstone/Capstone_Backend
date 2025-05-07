package com.waitit.capstone.domain.admin;

import com.waitit.capstone.domain.admin.dto.AllHostRequest;
import com.waitit.capstone.domain.admin.dto.AllUserRequest;
import com.waitit.capstone.domain.admin.dto.UpdatedRequest;

import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostMapper;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.member.Entity.Member;
import com.waitit.capstone.domain.member.MemberRepository;
import com.waitit.capstone.global.util.PageResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Service
public class AdminService {

    private final MemberRepository memberRepository;
    private final AdminMapper adminMapper;
    private final HostRepository hostRepository;
    private final HostMapper hostMapper;
    //모든 유저를 조회후 페이징
    public PageResponse<AllUserRequest> getAllUser(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        Page<AllUserRequest> allUserRequests = members.map(adminMapper::toAllUserRequest);

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
    //이벤트 배너 조회

    //모든 대기열 내역 조회
    public PageResponse<AllHostRequest> getAllHost(Pageable pageable){
        Page<Host> hosts = hostRepository.findAll(pageable);
        Page<AllHostRequest> allHostRequests = hosts.map(adminMapper::toAllHostRequest);

        return new PageResponse<>(allHostRequests);
    }
}

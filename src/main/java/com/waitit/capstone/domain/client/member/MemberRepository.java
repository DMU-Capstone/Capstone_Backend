package com.waitit.capstone.domain.client.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Boolean existsByNickname(String nickname);

    Member findByPhoneNumber(String phoneNumber);

    Member findMemberByPhoneNumber(String phoneNumber);
}

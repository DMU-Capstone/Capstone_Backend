//package com.waitit.capstone.domain.admin;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//
//import com.waitit.capstone.domain.client.member.Entity.Gender;
//import com.waitit.capstone.domain.client.member.Entity.Member;
//import com.waitit.capstone.domain.client.member.Entity.Role;
//import com.waitit.capstone.domain.client.member.MemberRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//class AdminServiceTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//    @Autowired
//    private AdminService adminService;
//
//    @BeforeEach
//    public void setUp(){
//        memberRepository = mock(MemberRepository.class);
//        adminService = new AdminService(memberRepository);
//        // given
//        Member member1 = Member.builder()
//                .name("홍길동")
//                .nickname("길동이")
//                .password("암호1")
//                .phoneNumber("01012345678")
//                .gender(Gender.MALE)
//                .role(Role.USER)
//                .build();
//
//        Member member2 = Member.builder()
//                .name("김영희")
//                .nickname("영희")
//                .password("암호2")
//                .phoneNumber("01087654321")
//                .gender(Gender.FEMALE)
//                .role(Role.USER)
//                .build();
//
//    }
//    @Test
//    void getAllUser() {
//    }
//
//    @Test
//    void updateMember() {
//    }
//
//    @Test
//    void deleteMember() {
//    }
//}
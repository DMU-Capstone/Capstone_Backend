package com.waitit.capstone.domain.admin;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.waitit.capstone.domain.client.member.Entity.Gender;
import com.waitit.capstone.domain.client.member.Entity.Member;
import com.waitit.capstone.domain.client.member.Entity.Role;
import com.waitit.capstone.domain.client.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void getAllUser() {
    }

    @Test
    void updateMember() {
    }

    @Test
    void deleteMember() {
    }
}
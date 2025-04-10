package com.waitit.capstone.domain.client.member.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String nickname;
    private String password;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime create_time;

    @PrePersist
    protected void onCreate() {
        this.create_time = LocalDateTime.now();
    }

    @Builder
    public Member(String name, String nickName, String password, String phoneNumber, Gender gender, Role role) {
        this.name = name;
        this.nickname = nickName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.role = Role.USER;
    }

    public void updateProfile(String name, String nickname, String password) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
    }
}
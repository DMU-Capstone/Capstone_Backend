package com.waitit.capstone.domain.dashboard.entity;

import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.member.Entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    // 비회원 방문 기록을 위해 Member는 nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @CreationTimestamp
    @Column(name = "visited_at")
    private LocalDateTime visitedAt;

    @Builder
    public VisitHistory(Host host, Member member, String phoneNumber) {
        this.host = host;
        this.member = member;
        this.phoneNumber = phoneNumber;
    }
}

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
@Table(name = "queue_cancellation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QueueCancellation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String reason;

    @CreationTimestamp
    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Builder
    public QueueCancellation(Host host, Member member, String phoneNumber, String reason) {
        this.host = host;
        this.member = member;
        this.phoneNumber = phoneNumber;
        this.reason = reason;
    }
}

package com.waitit.capstone.domain.dashboard.entity;

import com.waitit.capstone.domain.manager.Host;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "queue_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QueueLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Reason reason;

    @CreationTimestamp
    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "entered_at")
    private LocalDateTime enteredAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    public enum Status {
        WAITING, ENTERED, CANCELLED
    }

    public enum Reason {
        지루해서, 다른_일정과_겹침, 기타
    }

    @Builder
    public QueueLog(Host host, String phoneNumber) {
        this.host = host;
        this.phoneNumber = phoneNumber;
        this.status = Status.WAITING;
    }

    // 상태 변경 메소드들
    public void markAsEntered() {
        this.status = Status.ENTERED;
        this.enteredAt = LocalDateTime.now();
    }

    public void markAsCancelled(Reason reason) {
        this.status = Status.CANCELLED;
        this.reason = reason;
        this.cancelledAt = LocalDateTime.now();
    }
}

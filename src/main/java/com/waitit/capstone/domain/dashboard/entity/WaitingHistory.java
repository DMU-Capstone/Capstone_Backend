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
@Table(name = "waiting_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WaitingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @Column(name = "queue_size", nullable = false)
    private int queueSize;

    @CreationTimestamp
    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Builder
    public WaitingHistory(Host host, int queueSize) {
        this.host = host;
        this.queueSize = queueSize;
    }
}

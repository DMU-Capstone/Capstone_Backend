package com.waitit.capstone.domain.dashboard.entity;

import com.waitit.capstone.domain.manager.Host;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "promotion_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PromotionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @Column(nullable = false)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder
    public PromotionEvent(Host host, String title, LocalDate startDate, LocalDate endDate) {
        this.host = host;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}

package com.waitit.capstone.domain.image.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.waitit.capstone.domain.manager.Host;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "host_image")
public class HostImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "is_representative")
    private boolean isRepresentative;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    @JsonIgnore
    private Host host;

    private HostImage(Host host, String imagePath) {
        this.host = host;
        this.imagePath = imagePath;
        this.createdAt = LocalDateTime.now();
        this.isRepresentative = false; // 생성 시 기본값은 false
    }

    public static HostImage of(Host host, String imagePath) {
        return new HostImage(host, imagePath);
    }
}

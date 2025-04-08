package com.waitit.capstone.domain.client.manager;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Host {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl;

    private String hostName;

    private Integer maxPeople;

    private String hostManagerName;

    private String hostPhoneNumber;

    private Double latitude;
    private Double longitude;

    private String keyword;

    private String description;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

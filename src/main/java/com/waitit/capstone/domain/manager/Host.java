package com.waitit.capstone.domain.manager;

import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.manager.dto.CoordinateDto;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Host") // 테이블명을 명시적으로 지정
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Host {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
            mappedBy = "host",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<HostImage> images = new ArrayList<>();

    @Column(name = "host_name")
    private String hostName;

    @Column(name = "max_people")
    private Integer maxPeople;

    @Column(name = "host_manager_name")
    private String hostManagerName;

    @Column(name = "host_phone_number")
    private String hostPhoneNumber;

    @Column(name = "address")
    private String address; 

    @Column(name = "station")
    private String station; 

    @Column(name = "distance")
    private String distance; 

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "description")
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "is_active")
    private boolean isActive;

    public void addImage(HostImage img) {
        images.add(img);
        img.setHost(this);
    }

    public void removeImage(HostImage img) {
        images.remove(img);
        img.setHost(null);
    }

    public CoordinateDto getLatLong(){
        return new CoordinateDto(this.latitude,this.longitude);
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
}

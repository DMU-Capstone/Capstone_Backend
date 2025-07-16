package com.waitit.capstone.domain.manager;

import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.manager.dto.CoordinateDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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
}

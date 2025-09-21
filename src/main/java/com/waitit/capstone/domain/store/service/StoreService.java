package com.waitit.capstone.domain.store.service;

import com.waitit.capstone.domain.admin.dto.UpdateStoreRequest;
import com.waitit.capstone.domain.image.entity.HostImage;
import com.waitit.capstone.domain.image.repository.HostImageRepository;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.store.dto.LocationDto;
import com.waitit.capstone.domain.store.dto.OperatingHoursDto;
import com.waitit.capstone.domain.store.dto.StoreDetailResponse;
import com.waitit.capstone.domain.store.dto.StoreSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final HostRepository hostRepository;
    private final HostImageRepository hostImageRepository;

    public List<StoreSummaryResponse> getStores() {
        return hostRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }

    public StoreDetailResponse getStoreDetails(Long id) {
        Host host = hostRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 매장을 찾을 수 없습니다: " + id));
        return toDetailResponse(host);
    }

    @Transactional
    public void updateStoreSummary(UpdateStoreRequest request) {
        Host host = hostRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 매장을 찾을 수 없습니다: " + request.getId()));

        host.setHostName(request.getTitle());

        host.getImages().stream()
                .filter(HostImage::isRepresentative)
                .findFirst()
                .ifPresent(img -> img.setRepresentative(false));

        HostImage newRepresentativeImage = hostImageRepository.findByImagePath(request.getImgUrl())
                .orElseThrow(() -> new IllegalArgumentException("해당 이미지 URL을 찾을 수 없습니다: " + request.getImgUrl()));

        if (!newRepresentativeImage.getHost().getId().equals(host.getId())) {
            throw new IllegalArgumentException("해당 이미지는 다른 매장의 이미지입니다.");
        }
        newRepresentativeImage.setRepresentative(true);
    }

    private StoreSummaryResponse toSummaryResponse(Host host) {
        String imageUrl = host.getImages().stream()
                .filter(HostImage::isRepresentative)
                .findFirst()
                .map(HostImage::getImagePath)
                .orElse(host.getImages().isEmpty() ? null : host.getImages().get(0).getImagePath());

        return StoreSummaryResponse.builder()
                .id(host.getId())
                .imgUrl(imageUrl)
                .title(host.getHostName())
                .build();
    }

    private StoreDetailResponse toDetailResponse(Host host) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        List<String> imageUrls = host.getImages().stream()
                .map(HostImage::getImagePath)
                .collect(Collectors.toList());

        LocationDto locationDto = LocationDto.builder()
                .address(host.getAddress())
                .station(host.getStation())
                .distance(host.getDistance())
                .latitude(host.getLatitude())
                .longitude(host.getLongitude())
                .build();

        OperatingHoursDto operatingHoursDto = OperatingHoursDto.builder()
                .open(host.getStartTime() != null ? host.getStartTime().format(timeFormatter) : null)
                .close(host.getEndTime() != null ? host.getEndTime().format(timeFormatter) : null)
                .build();

        return StoreDetailResponse.builder()
                .id(host.getId())
                .name(host.getHostName())
                .description(host.getDescription())
                .images(imageUrls)
                .location(locationDto)
                .operating_hours(operatingHoursDto)
                .build();
    }
}

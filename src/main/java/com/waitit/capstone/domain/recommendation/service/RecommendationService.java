package com.waitit.capstone.domain.recommendation.service;

import com.waitit.capstone.domain.kakao.dto.KakaoSearchResponse;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.service.QueueService;
import com.waitit.capstone.domain.recommendation.dto.NearbyHostResponse;
import com.waitit.capstone.domain.recommendation.dto.RecommendationResponse;
import com.waitit.capstone.domain.kakao.service.KakaoMapsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final HostRepository hostRepository;
    private final QueueService queueService;
    private final KakaoMapsService kakaoMapsService;

    private static final int AVG_WAIT_TIME_PER_PERSON = 5;
    private static final int SHORT_WAIT_THRESHOLD = 30;
    private static final int SEARCH_RADIUS = 1000; // 1km

    private static final String CATEGORY_CAFE = "CE7";
    private static final String CATEGORY_ATTRACTION = "AT4";

    public RecommendationResponse recommend(String phoneNumber, double latitude, double longitude) {
        // [수정] DB가 아닌, 실제 대기열이 있는 가게 ID 목록을 QueueService에서 가져옴
        Set<Long> activeHostIds = queueService.getActiveHostIds();
        if (activeHostIds.isEmpty()) {
            // 활성화된 가게가 없으면, 대기 중이 아닌 것으로 간주하고 빈 주변 가게 목록 반환
            return RecommendationResponse.builder()
                    .isWaiting(false)
                    .hostRecommendations(List.of())
                    .build();
        }
        List<Host> activeHosts = hostRepository.findAllById(activeHostIds);

        // 사용자가 대기 중인 가게를 찾음
        Optional<Host> waitingHostOptional = activeHosts.stream()
                .filter(host -> {
                    // getMyPosition은 0보다 큰 값을 반환하면 대기 중임을 의미
                    return queueService.getMyPosition(host.getId(), QueueDto.builder().phoneNumber(phoneNumber).build()) > 0;
                })
                .findFirst();

        // 1. 사용자가 대기 중일 경우
        if (waitingHostOptional.isPresent()) {
            Host waitingHost = waitingHostOptional.get();
            int myPosition = queueService.getMyPosition(waitingHost.getId(), QueueDto.builder().phoneNumber(phoneNumber).build());
            int estimatedWaitTime = myPosition * AVG_WAIT_TIME_PER_PERSON;

            KakaoSearchResponse recommendations;
            if (estimatedWaitTime < SHORT_WAIT_THRESHOLD) {
                recommendations = kakaoMapsService.searchByCategory(CATEGORY_ATTRACTION, latitude, longitude, SEARCH_RADIUS);
            } else {
                recommendations = kakaoMapsService.searchByCategory(CATEGORY_CAFE, latitude, longitude, SEARCH_RADIUS);
            }
            return RecommendationResponse.builder()
                    .isWaiting(true)
                    .placeRecommendations(recommendations)
                    .build();
        } 
        // 2. 사용자가 대기 중이 아닐 경우
        else {
            List<NearbyHostResponse> nearbyHosts = activeHosts.stream()
                    .map(host -> {
                        double distance = calculateDistance(latitude, longitude, host.getLatitude(), host.getLongitude());
                        int waitingCount = queueService.getQueueByHostId(host.getId()).size();
                        return NearbyHostResponse.from(host, waitingCount, distance);
                    })
                    .filter(nearbyHost -> nearbyHost.getDistance() <= SEARCH_RADIUS)
                    .sorted((h1, h2) -> Integer.compare(h1.getDistance(), h2.getDistance()))
                    .collect(Collectors.toList());
            
            return RecommendationResponse.builder()
                    .isWaiting(false)
                    .hostRecommendations(nearbyHosts)
                    .build();
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}

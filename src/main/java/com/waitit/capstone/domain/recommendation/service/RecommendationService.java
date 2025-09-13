package com.waitit.capstone.domain.recommendation.service;

import com.waitit.capstone.domain.kakao.service.KakaoMapsService;
import com.waitit.capstone.domain.manager.Host;
import com.waitit.capstone.domain.manager.HostRepository;
import com.waitit.capstone.domain.queue.dto.QueueDto;
import com.waitit.capstone.domain.queue.service.QueueService;
import com.waitit.capstone.domain.recommendation.dto.NearbyHostResponse;
import com.waitit.capstone.domain.recommendation.dto.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final HostRepository hostRepository;
    private final QueueService queueService;
    private final KakaoMapsService kakaoMapsService;
    private final RedissonClient redissonClient;

    private static final int AVG_WAIT_TIME_PER_PERSON = 5;
    private static final int SHORT_WAIT_THRESHOLD = 30;
    private static final int SEARCH_RADIUS = 1000; // 1km

    private static final String CATEGORY_CAFE = "CE7";
    private static final String CATEGORY_ATTRACTION = "AT4";

    public RecommendationResponse recommend(String phoneNumber, double latitude, double longitude) {
        List<Host> activeHosts = hostRepository.findAllByIsActive(true);

        Optional<Host> waitingHostOptional = activeHosts.stream()
                .filter(host -> {
                    QueueDto userDto = QueueDto.builder().phoneNumber(phoneNumber).name("user").count(1).build();
                    return queueService.getMyPosition(host.getId(), userDto) > 0;
                })
                .findFirst();

        // 1. 사용자가 대기 중일 경우
        if (waitingHostOptional.isPresent()) {
            Host waitingHost = waitingHostOptional.get();
            QueueDto userDto = QueueDto.builder().phoneNumber(phoneNumber).name("user").count(1).build();
            int myPosition = queueService.getMyPosition(waitingHost.getId(), userDto);
            int estimatedWaitTime = myPosition * AVG_WAIT_TIME_PER_PERSON;

            Object recommendations;
            if (estimatedWaitTime < SHORT_WAIT_THRESHOLD) {
                recommendations = kakaoMapsService.searchByCategory(CATEGORY_ATTRACTION, latitude, longitude, SEARCH_RADIUS);
            } else {
                recommendations = kakaoMapsService.searchByCategory(CATEGORY_CAFE, latitude, longitude, SEARCH_RADIUS);
            }
            return RecommendationResponse.of(true, recommendations);
        } 
        // 2. 사용자가 대기 중이 아닐 경우
        else {
            List<NearbyHostResponse> nearbyHosts = activeHosts.stream()
                    .map(host -> {
                        double distance = calculateDistance(latitude, longitude, host.getLatitude(), host.getLongitude());
                        RList<QueueDto> queue = redissonClient.getList("waitList:" + host.getId());
                        return new Object[]{host, distance, queue.size()};
                    })
                    .filter(obj -> (double) obj[1] <= SEARCH_RADIUS) // 대기열 조건 제거
                    .sorted((obj1, obj2) -> Double.compare((double) obj1[1], (double) obj2[1]))
                    .map(obj -> NearbyHostResponse.from((Host) obj[0], (int) obj[2], (double) obj[1]))
                    .collect(Collectors.toList());
            return RecommendationResponse.of(false, nearbyHosts);
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

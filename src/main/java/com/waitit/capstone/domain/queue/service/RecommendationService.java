package com.waitit.capstone.domain.queue.service;

import com.waitit.capstone.domain.manager.dto.CoordinateDto;
import com.waitit.capstone.domain.queue.dto.PlaceDto;
import com.waitit.capstone.domain.queue.dto.PlaceResponseDto;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@AllArgsConstructor
public class RecommendationService {

    private final RestTemplate restTemplate;
    @Value("${kakao.api.key}")
    private final String kakaoApiKey;
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/keyword.json";

    /**
     * 대기 시간에 따라 주변 장소를 추천하는 메서드입니다.
     * @param coordinateDto 사용자의 현재 위치 좌표 (위도, 경도)
     * @param waitingTime 분 단위의 대기 시간
     * @return 추천 장소 목록 DTO
     */
    public PlaceResponseDto recommendPlace(CoordinateDto coordinateDto, int waitingTime){
        List<String> keywords = getKeywordsByTime(waitingTime);

        List<PlaceDto> places = keywords.parallelStream()
                .flatMap(keyword->searchPlacesByKeyword(coordinateDto,keyword).stream())
                .toList();
        return new PlaceResponseDto(places);
    }
    /**
     * 대기 시간에 따라 추천 장소 키워드 목록을 반환합니다.
     * @param minute 분 단위의 대기 시간
     * @return 장소 검색을 위한 키워드 리스트
     */
    private List<String> getKeywordsByTime(int minute) {
        if (isOverOneHour(minute)) {
            // ⏰ 1시간 이상 웨이팅 시 추천 키워드
            return Arrays.asList("카페", "다이소", "서점", "공원", "산책", "팝업스토어", "갤러리");
        } else {
            // 🏃 30분 내외 웨이팅 시 추천 키워드
            return Arrays.asList("편의점", "올리브영", "소품샵", "액세서리", "서점", "인생네컷");
        }
    }
    /**
     * 특정 키워드와 좌표를 사용하여 카카오 로컬 API로 장소를 검색합니다.
     * @param coordinateDto 사용자 위치 좌표
     * @param keyword 검색할 키워드
     * @return 검색된 장소 DTO 리스트
     */
    private List<PlaceDto> searchPlacesByKeyword(CoordinateDto coordinateDto, String keyword) {
        URI targetUrl = UriComponentsBuilder
                .fromUriString(KAKAO_API_URL)
                .queryParam("query", keyword)
                .queryParam("y", coordinateDto.latitude())  // 위도
                .queryParam("x", coordinateDto.longitude()) // 경도
                .queryParam("radius", 1000) // 반경 1km 이내
                .queryParam("sort", "distance") // 거리순 정렬
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();

        // Kakao API 호출을 위한 HttpHeaders 설정
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

        // API 호출 및 결과 파싱
        KakaoApiResponse response = restTemplate.exchange(targetUrl, org.springframework.http.HttpMethod.GET, entity, KakaoApiResponse.class).getBody();

        if (response != null && response.getDocuments() != null) {
            // API 응답을 PlaceDto 리스트로 변환합니다.
            return response.getDocuments().stream()
                    .map(doc -> new PlaceDto(
                            doc.getPlace_name(),
                            doc.getDistance(),
                            doc.getPlace_url(),
                            doc.getCategory_name(),
                            doc.getAddress_name(),
                            doc.getRoad_address_name(),
                            doc.getX(),
                            doc.getY()))
                    .toList();
        }
        return List.of(); // 결과가 없을 경우 빈 리스트 반환
    }

    /**
     * 대기 시간이 1시간 이상인지 확인합니다.
     * @param minute 분 단위의 대기 시간
     * @return 60분 이상이면 true, 미만이면 false
     */
    private boolean isOverOneHour(int minute){
        return minute >= 60;
    }

    /**
     * 카카오 API 응답 전체를 파싱하기 위한 DTO
     */
    @Setter
    @Getter
    @AllArgsConstructor
    private static class KakaoApiResponse {
        private List<Document> documents;

    }

    /**
     * 카카오 API 응답 내 'documents' 필드의 각 항목을 파싱하기 위한 DTO
     */
    @Getter
    private static class Document {
        // Getters
        private String place_name;
        private String distance;
        private String place_url;
        private String category_name;
        private String address_name;
        private String road_address_name;
        private String x; // longitude
        private String y; // latitude

    }
}

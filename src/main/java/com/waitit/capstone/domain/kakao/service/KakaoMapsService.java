package com.waitit.capstone.domain.kakao.service;

import com.waitit.capstone.domain.kakao.dto.KakaoSearchResponse;
import com.waitit.capstone.domain.kakao.dto.PlaceDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class KakaoMapsService {

    // private final RestTemplate restTemplate; // 주석 처리



    private static final String KAKAO_CATEGORY_SEARCH_URL = "https://dapi.kakao.com/v2/local/search/category.json";

    public KakaoSearchResponse searchByCategory(String categoryCode, double latitude, double longitude, int radius) {
        /*
        // 실제 API 호출 로직 주석 처리
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_CATEGORY_SEARCH_URL)
                .queryParam("category_group_code", categoryCode)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", radius)
                .queryParam("sort", "distance");

        URI uri = uriBuilder.build().encode(StandardCharsets.UTF_8).toUri();

        ResponseEntity<KakaoSearchResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, KakaoSearchResponse.class);
        
        return response.getBody();
        */

        // 더미 데이터 생성 및 반환
        List<PlaceDocument> dummyDocuments = createDummyDocuments(categoryCode, 20);
        return new KakaoSearchResponse(dummyDocuments);
    }

    /**
     * 테스트용 더미 장소 데이터 목록을 생성합니다.
     * @param categoryCode 카테고리 코드 (추천 종류를 구분하기 위함)
     * @param count 생성할 데이터 개수
     * @return PlaceDocument 리스트
     */
    private List<PlaceDocument> createDummyDocuments(String categoryCode, int count) {
        Random random = new Random();
        String[] cafeNames = {"스타벅스", "투썸플레이스", "이디야커피", "메가커피", "컴포즈커피", "빽다방"};
        String[] attractionNames = {"올리브영", "다이소", "아트박스", "카카오프렌즈샵", "인생네컷", "못된고양이"};

        String[] names = categoryCode.equals("CE7") ? cafeNames : attractionNames;
        String category = categoryCode.equals("CE7") ? "음식점 > 카페" : "소매 > 잡화점";

        return IntStream.range(1, count + 1)
                .mapToObj(i -> {
                    String placeName = names[random.nextInt(names.length)] + " " + i + "호점";
                    return PlaceDocument.builder()
                            .placeName(placeName)
                            .distance(String.valueOf(random.nextInt(1000) + 50)) // 50m ~ 1050m
                            .placeUrl("http://place.map.kakao.com/" + (10000000 + i))
                            .categoryName(category)
                            .addressName("서울 강남구 역삼동 123-" + i)
                            .roadAddressName("서울 강남구 테헤란로 1길 " + i)
                            .longitude(String.valueOf(127.0276 + (random.nextDouble() - 0.5) * 0.01))
                            .latitude(String.valueOf(37.4979 + (random.nextDouble() - 0.5) * 0.01))
                            .build();
                })
                .collect(Collectors.toList());
    }
}

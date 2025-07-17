package com.waitit.capstone.domain.queue.service;

import com.waitit.capstone.domain.manager.dto.CoordinateDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class RecommendationService {

    private final RestTemplate restTemplate;

    //좌표위치와 시간을 받아서 가야 할 장소 판단

    //api 호출해서 지역 목록 반환

    private boolean isOverOneHour(int minute){
        return minute >= 60;
    }

    public
}
/** 30분
 * 편의점 / 드럭스토어 (올리브영 등)
 * 소품샵 / 액세서리 가게
 * 서점 /
 * 즉석 사진 부스 (인생네컷 등)
 *
 * ⏰ 1시간 웨이팅
 * 근처 다른 카페
 * 다이소 /서점
 * 가까운 공원 또는 산책로
 * 팝업 스토어 또는 작은 갤러리 (성수, 연남 등 핫플이라면!)
 */
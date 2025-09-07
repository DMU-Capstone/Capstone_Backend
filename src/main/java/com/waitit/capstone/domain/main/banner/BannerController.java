package com.waitit.capstone.domain.main.banner;

import com.waitit.capstone.domain.admin.dto.MainBannerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "배너 API", description = "메인 화면 배너 관련 API")
public class BannerController {

    private final BannerService bannerService;

    @Operation(summary = "이벤트 배너 조회", description = "메인 화면에 표시될 이벤트 배너를 조회합니다.")
    @GetMapping("/event/select")
    public ResponseEntity<MainBannerResponse> getAllBanner(){
        MainBannerResponse responseList = bannerService.getEventBanner();
        return ResponseEntity.ok(responseList);
    }
}

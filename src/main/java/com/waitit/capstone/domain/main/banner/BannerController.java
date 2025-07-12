package com.waitit.capstone.domain.main.banner;

import com.waitit.capstone.domain.admin.dto.MainBannerResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/event/select")
    public ResponseEntity<MainBannerResponse> getAllBanner(){
        MainBannerResponse responseList = bannerService.getEventBanner();
        return ResponseEntity.ok(responseList);
    }
}

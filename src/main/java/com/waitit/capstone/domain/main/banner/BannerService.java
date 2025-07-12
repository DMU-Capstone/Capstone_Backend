package com.waitit.capstone.domain.main.banner;

import com.waitit.capstone.domain.admin.dto.MainBannerResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BannerService {

    private final StringRedisTemplate redisTemplate;

    public MainBannerResponse getEventBanner() {
        String redisKey = "main_banner";
        List<String> list = redisTemplate.opsForList().range(redisKey, 0, 4);
        return new MainBannerResponse("mainBanner", list);
    }
}

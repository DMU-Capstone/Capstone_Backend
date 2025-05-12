package com.waitit.capstone.global.common;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisInitializer {
    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    public void initList() {
        String LIST_KEY = "main_banner";
        if (Boolean.FALSE.equals(redisTemplate.hasKey(LIST_KEY))) {
            redisTemplate.opsForList().leftPushAll(LIST_KEY, "default1","default2","default3","default4","default5");
        }
    }
}
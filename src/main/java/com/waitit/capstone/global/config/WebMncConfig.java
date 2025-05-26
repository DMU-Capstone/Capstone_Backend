package com.waitit.capstone.global.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMncConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 클라이언트 요청: /uploads/** → 실제 파일 시스템 경로로 매핑
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/home/ubuntu/app/uploads/");
    }
}

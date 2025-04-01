package com.waitit.capstone.global.security.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenResolver {
    public String resolve(HttpServletRequest request) {
        String refreshToken = null;

        // 모바일용 (헤더)
        String headerToken = request.getHeader("Refresh-Token");
        if (headerToken != null) {
            refreshToken = headerToken;
        }

        // 웹용 (쿠키)
        if (refreshToken == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        return refreshToken;
    }
}

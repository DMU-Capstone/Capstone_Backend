package com.waitit.capstone.global.security.jwt;

import com.waitit.capstone.domain.client.auth.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

@AllArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenResolver refreshTokenResolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 로그아웃 요청이 아니면 그냥 통과
        if (!request.getRequestURI().equals("/logout") || !request.getMethod().equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        //Refresh Token 추출
        String refresh = refreshTokenResolver.resolve(request);

        if (refresh == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //만료 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Refresh 토큰인지 확인
        if (!"refresh".equals(jwtUtil.getCategory(refresh))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //유효성 확인
        String username = jwtUtil.getUsername(refresh);
        boolean isValid = refreshTokenService.isValid(username, refresh);

        if (!isValid) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //Redis에서 Refresh 토큰 제거
        refreshTokenService.delete(username);

        //쿠키 만료 처리 (웹용)
        Cookie expiredCookie = new Cookie("refresh", null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        response.addCookie(expiredCookie);

        response.setStatus(HttpServletResponse.SC_OK);
    }
}

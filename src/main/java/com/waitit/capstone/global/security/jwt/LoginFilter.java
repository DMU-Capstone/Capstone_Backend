package com.waitit.capstone.global.security.jwt;

import com.waitit.capstone.domain.auth.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waitit.capstone.domain.auth.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
@AllArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // 클라이언트 요청에서 username, password 추출
        LoginRequest loginRequest = new LoginRequest();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("사용자 번호 {}", loginRequest.getUsername());

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        // 인증을 위해 토큰으로 변환
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(token);
    }

    // 로그인 성공 시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String name = refreshTokenService.findMember(username);
        long accessExpireMs = 600000L;
        long refreshExpireMs = 86400000L;

        String access = jwtUtil.createJwt("access", username, role, accessExpireMs);
        String refresh = jwtUtil.createJwt("refresh", username, role, refreshExpireMs);

        // Refresh 토큰 저장
        refreshTokenService.save(username, refresh, refreshExpireMs);

        // 클라이언트 타입 구분
        String clientType = request.getHeader("X-Client-Type");

        // 공통: access token은 헤더에 설정 (REST 표준)
        response.setHeader("Authorization", "Bearer " + access);

        if ("mobile".equalsIgnoreCase(clientType)) {
            // 모바일: JSON 응답
            String jsonResponse = "{\"message\": \"로그인에 성공했습니다.\", " +
                    "\"username\": \"" + username + "\", " +
                    "\"name\": \"" + name + "\", " +
                    "\"role\": \"" + role + "\", " +
                    "\"refresh\": \"" + refresh + "\"}";
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse);
        } else {
            // 웹: HttpOnly 쿠키로 refresh 토큰 전달
            Cookie refreshCookie = jwtUtil.createCookie("refresh", refresh);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            response.addCookie(refreshCookie);
            String jsonResponse = "{\"message\": \"로그인에 성공했습니다.\", " +
                    "\"username\": \"" + username + "\", " +
                    "\"name\": \"" + name + "\", " +
                    "\"role\": \"" + role + "\"}";
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonResponse);
            response.setStatus(HttpStatus.OK.value());
        }
    }

    // 로그인 실패 시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"잘못된 번호 혹은 비밀번호 입니다.\"}");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}

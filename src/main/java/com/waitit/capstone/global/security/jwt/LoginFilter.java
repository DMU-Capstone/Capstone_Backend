package com.waitit.capstone.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waitit.capstone.domain.auth.dto.LoginRequest;
import com.waitit.capstone.domain.auth.dto.LoginResponseDto;
import com.waitit.capstone.domain.auth.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshTokenService refreshTokenService, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        LoginRequest loginRequest = new LoginRequest();
        try {
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequest = objectMapper.readValue(messageBody, LoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("사용자 번호 {}", loginRequest.getUsername());

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password, null);
        return authenticationManager.authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authentication) throws IOException {
        String phoneNumber = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String name = refreshTokenService.findMember(phoneNumber);
        long accessExpireMs = 600000L;
        long refreshExpireMs = 86400000L;

        String access = jwtUtil.createJwt("access", phoneNumber, role, accessExpireMs);
        String refresh = jwtUtil.createJwt("refresh", phoneNumber, role, refreshExpireMs);

        refreshTokenService.save(phoneNumber, refresh, refreshExpireMs);

        response.setHeader("Authorization", "Bearer " + access);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String clientType = request.getHeader("X-Client-Type");
        LoginResponseDto loginResponse;

        if ("mobile".equalsIgnoreCase(clientType)) {
            loginResponse = LoginResponseDto.builder()
                    .message("로그인에 성공했습니다.")
                    .phoneNumber(phoneNumber)
                    .name(name)
                    .role(role)
                    .refresh(refresh)
                    .build();
        } else {
            Cookie refreshCookie = jwtUtil.createCookie("refresh", refresh);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            response.addCookie(refreshCookie);

            loginResponse = LoginResponseDto.builder()
                    .message("로그인에 성공했습니다.")
                    .phoneNumber(phoneNumber)
                    .name(name)
                    .role(role)
                    .build();
        }

        response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
        response.setStatus(HttpStatus.OK.value());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"잘못된 번호 혹은 비밀번호 입니다.\"}");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}

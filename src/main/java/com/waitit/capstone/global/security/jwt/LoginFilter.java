package com.waitit.capstone.global.security.jwt;

import com.waitit.capstone.domain.client.auth.dto.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.waitit.capstone.domain.client.auth.service.RefreshTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

@Slf4j
@AllArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        //클라이언트 요청에서 username password 추출
        LoginRequest loginRequest = new LoginRequest();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequest  = objectMapper.readValue(messageBody, LoginRequest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("사용자 번호 {}",loginRequest.getUsername());

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        //인증을 위해 토큰으로 변환
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,password,null);

        return authenticationManager.authenticate(token);
    }
    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        //유저정보
        String username = authentication.getName();

        Collection<? extends  GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends  GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰생성
        String access = jwtUtil.createJwt("access",username, role ,600000L);
        String refresh = jwtUtil.createJwt("refresh",username, role ,86400000L);

        //리프레쉬 토큰 저장
        refreshTokenService.save(username, refresh, 86400000L);

        //응답 설정
        response.setHeader("access",access);
        response.addCookie(jwtUtil.createCookie("refresh",refresh));
        response.setStatus(HttpStatus.OK.value());

    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException {
        //커스텀 에러로 변경 필요
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"잘못된 번호 혹은 비밀번호 입니다.\"}");
    }
}

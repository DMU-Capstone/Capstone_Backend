package com.dumbbelling.capstone.global.security.jwt;

import com.dumbbelling.capstone.domain.client.auth.dto.CustomUserDetails;
import com.dumbbelling.capstone.domain.client.member.Member;
import com.dumbbelling.capstone.domain.client.member.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
@Slf4j
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");
        //Authorization 헤더 검증
        if(authorization == null || !authorization.startsWith("Bearer ")){
            log.info("token expired");
            filterChain.doFilter(request,response);

            return  ; //조건이 해당되면 메소드 종료 (필수)
        }
        log.info("authorization now");
        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        if(jwtUtil.isExpired(token)){
            log.info("token expired");
            filterChain.doFilter(request,response);

            return ; //조건이 해당되면 메소드 종료 (필수)
        }

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        //userEntity를 생성하여 값 set
        Member member = Member.builder()
                .phoneNumber(username)
                .password("tempPassword")
                .role(Role.USER)
                .build();

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        //스프링 시큐리티 인증 토큰 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails,null,customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request,response);
    }
}

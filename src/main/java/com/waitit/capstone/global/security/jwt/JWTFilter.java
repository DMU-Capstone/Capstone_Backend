package com.waitit.capstone.global.security.jwt;

import com.waitit.capstone.domain.client.auth.dto.CustomUserDetails;
import com.waitit.capstone.domain.client.member.Entity.Member;
import com.waitit.capstone.domain.client.member.Entity.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
        //헤더에서 엑세스 토큰을 꺼냄
        String accessToken = request.getHeader("access");

        //토큰이 없다면 다음 필터로 넘김
        if(accessToken == null){
            filterChain.doFilter(request,response);
            return;
        }
        //토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        try{jwtUtil.isExpired(accessToken);}
        catch (ExpiredJwtException e) {
            //response body
            PrintWriter writer = response.getWriter();
            writer.println("access token expired");

            //error code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //토큰이 엑세스인지 확인(발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // username, role 값을 획득
        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        Member member = Member.builder()
                .phoneNumber(username)
                .role(Role.USER)
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);


    }
}

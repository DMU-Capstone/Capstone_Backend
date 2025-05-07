package com.waitit.capstone.domain.auth.controller;

import com.waitit.capstone.domain.auth.service.RefreshTokenService;
import com.waitit.capstone.global.security.jwt.JWTUtil;
import com.waitit.capstone.global.security.jwt.RefreshTokenResolver;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@AllArgsConstructor
public class ReissueController {
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenResolver refreshTokenResolver;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        //리프레쉬 토큰 리졸버로 판단
        String refresh = refreshTokenResolver.resolve(request);

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        if (!"refresh".equals(jwtUtil.getCategory(refresh))) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        refreshTokenService.delete(username);
        refreshTokenService.save(username, newRefresh, 86400000L);

        response.setHeader("access", newAccess);
        response.addCookie(jwtUtil.createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

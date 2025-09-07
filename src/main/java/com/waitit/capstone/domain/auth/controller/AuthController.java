package com.waitit.capstone.domain.auth.controller;

import java.util.*;
import com.waitit.capstone.domain.auth.service.AuthService;
import com.waitit.capstone.domain.auth.dto.JoinRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Tag(name = "인증 API", description = "사용자 인증 관련 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "회원가입", description = "사용자가 회원가입을 요청합니다.")
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequest joinRequest){
        log.info("회원가입 요청: {}", joinRequest.getName());
        authService.join(joinRequest);

        //추후 공통 응답바디로 변경
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "회원가입에 성공하였습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);

    }
}

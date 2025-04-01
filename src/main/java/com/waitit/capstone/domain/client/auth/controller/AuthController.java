package com.waitit.capstone.domain.client.auth.controller;
import com.waitit.capstone.domain.client.auth.service.AuthService;
import com.waitit.capstone.domain.client.auth.dto.JoinRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequest joinRequest){
        log.info("회원가입 요청: {}", joinRequest.getName());
        authService.join(joinRequest);
        return ResponseEntity.ok("회원가입에 성공하였습니다.");
    }
}

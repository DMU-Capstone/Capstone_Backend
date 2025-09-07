package com.waitit.capstone.domain.message;

import com.waitit.capstone.domain.message.dto.SmsRequest;
import com.waitit.capstone.domain.message.dto.SmsVerifyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/sms")
@Tag(name = "SMS API", description = "SMS 문자 인증 관련 API")
public class SmsController {

    private final SmsService smsService;

    @Operation(summary = "인증번호 SMS 발송", description = "사용자에게 인증번호를 담은 SMS를 발송합니다.")
    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@RequestBody @Valid SmsRequest smsRequest){
        smsService.sendSms(smsRequest);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }

    @Operation(summary = "인증번호 확인", description = "사용자가 입력한 인증번호가 유효한지 확인합니다.")
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody @Valid SmsVerifyRequest smsVerifyRequest){
        boolean verify = smsService.verifyCode(smsVerifyRequest);

        if (verify) {
            Map<String,String> map = new HashMap<>();
            map.put("message","인증이 되었습니다.");
            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증에 실패했습니다.");
        }
    }

}

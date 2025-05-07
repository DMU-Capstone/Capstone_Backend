package com.waitit.capstone.domain.message;

import com.waitit.capstone.domain.message.dto.SmsRequest;
import com.waitit.capstone.domain.message.dto.SmsVerifyRequest;
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
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send")
    public ResponseEntity<?> SendSMS(@RequestBody @Valid SmsRequest smsRequest){
        smsService.sendSms(smsRequest);
        return ResponseEntity.ok("문자를 전송했습니다.");
    }
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

package com.waitit.capstone.domain.message;

import com.waitit.capstone.global.util.SmsCertificationUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SmsService {

    private final SmsCertificationUtil smsCertificationUtil;

    public void sendSms(SmsRequest smsRequest) {
        String phoneNum = smsRequest.getPhoneNum(); // Smsrequest에서 전화번호를 가져온다.
        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsCertificationUtil.sendSMS(phoneNum, certificationCode); // SMS 인증 유틸리티를 사용하여 SMS 발송
    }
}

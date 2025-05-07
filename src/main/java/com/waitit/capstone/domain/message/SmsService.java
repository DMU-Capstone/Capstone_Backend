package com.waitit.capstone.domain.message;

import com.waitit.capstone.domain.member.MemberRepository;
import com.waitit.capstone.domain.message.dto.SmsRequest;
import com.waitit.capstone.domain.message.dto.SmsVerifyRequest;
import com.waitit.capstone.global.util.SmsCertificationUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SmsService {

    private final SmsCertificationUtil smsCertificationUtil;
    private final SmsRepository smsRepository; // SMS 레포지토리 객체 (Redis)
    private final MemberRepository memberRepository;

    public void sendSms(SmsRequest smsRequest) {

        String phoneNum = smsRequest.getPhoneNum(); // SmsRequest에서 전화번호를 가져온다.
        if(memberRepository.existsByPhoneNumber(phoneNum)){
            throw new IllegalArgumentException("이미 가입된 번호입니다.");
        }

        String certificationCode = Integer.toString((int)(Math.random() * (999999 - 100000 + 1)) + 100000); // 6자리 인증 코드를 랜덤으로 생성
        smsCertificationUtil.sendSMS(phoneNum, certificationCode); // SMS 인증 유틸리티를 사용하여 SMS 발송
        smsRepository.createSmsCertification(phoneNum, certificationCode); // 인증 코드를 Redis에 저장
    }

    public boolean verifyCode(@Valid SmsVerifyRequest smsVerifyRequest) {
        if (isVerify(smsVerifyRequest.getPhoneNum(), smsVerifyRequest.getCertificationCode())) { // 인증 코드 검증
            smsRepository.deleteSmsCertification(smsVerifyRequest.getPhoneNum()); // 검증이 성공하면 Redis에서 인증 코드 삭제
            return true; // 인증 성공 반환
        } else {
            return false; // 인증 실패 반환
        }
    }
    // 전화번호와 인증 코드를 검증하는 메서드
    private boolean isVerify(String phoneNum, String certificationCode) {
        return smsRepository.hasKey(phoneNum) && // 전화번호에 대한 키가 존재하고
                smsRepository.getSmsCertification(phoneNum).equals(certificationCode); // 저장된 인증 코드와 입력된 인증 코드가 일치하는지 확인
    }
}

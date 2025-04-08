package com.waitit.capstone.domain.client.auth.service;

import com.waitit.capstone.domain.client.member.Entity.Member;
import com.waitit.capstone.domain.client.member.MemberRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String,String> redisTemplate;
    private static final String REFRESH_PREFIX = "refresh";
    private final MemberRepository memberRepository;

    public void save(String username,String refresh,Long expireMS){
        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + username,
                refresh,
                Duration.ofMillis(expireMS)
        );
    }

    public boolean isValid(String username,String refreshToken){
        String stored = redisTemplate.opsForValue().get(REFRESH_PREFIX + username);
        return stored != null && stored.equals(refreshToken);
    }

    public void delete(String username){
        redisTemplate.delete(REFRESH_PREFIX + username);
    }
    public String findMember(String phone){
        Member member = memberRepository.findMemberByPhoneNumber(phone);
        return member.getName();
    }
}

package com.waitit.capstone.domain.auth.service;

import com.waitit.capstone.domain.auth.dto.CustomOAuth2User;
import com.waitit.capstone.domain.auth.dto.KakaoResponse;
import com.waitit.capstone.domain.auth.dto.OAuth2Response;
import com.waitit.capstone.domain.auth.dto.UserDto;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException{

        OAuth2User oAuth2User = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();

        UserDto userDTO = new UserDto();
        userDTO.setUsername(username);
        userDTO.setName(oAuth2Response.getName());
        userDTO.setRole("ROLE_USER");

        return new CustomOAuth2User(userDTO);
    }

}

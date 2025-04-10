package com.hnc.mogak.global.auth.oauth;

import com.hnc.mogak.global.auth.oauth.userinfo.KakaoUserInfo;
import com.hnc.mogak.global.auth.oauth.userinfo.NaverUserInfo;
import com.hnc.mogak.global.auth.oauth.userinfo.OAuth2UserInfo;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.AuthException;
import com.hnc.mogak.member.application.port.out.LoadMemberPort;
import com.hnc.mogak.member.application.port.out.PersistMemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final LoadMemberPort loadMemberPort;
    private final PersistMemberPort persistMemberPort;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String attributeKey = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oAuth2UserInfo = getOAuth2UserInfo(provider, oAuth2UserAttributes, attributeKey);
        Member member = loadOrPersistMember(oAuth2UserInfo);
        return new PrincipalDetails(member, oAuth2UserAttributes, attributeKey);
    }

    private Member loadOrPersistMember(OAuth2UserInfo oAuth2UserInfo) {
        String providerId = oAuth2UserInfo.getProviderId();
        Member member;
        if (loadMemberPort.existsByProviderId(providerId)) {
            member = loadMemberPort.loadMemberByProviderId(oAuth2UserInfo.getProviderId());
        } else {
            member = oAuth2UserInfo.mapToMember();
            persistMemberPort.persistMember(member);
        }

        return member;
    }

    private static OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> oAuth2UserAttributes, String attributeKey) {
        OAuth2UserInfo oAuth2UserInfo;
        switch (provider) {
            case "kakao" -> oAuth2UserInfo = new KakaoUserInfo(oAuth2UserAttributes);
            case "naver" -> oAuth2UserInfo = new NaverUserInfo(oAuth2UserAttributes, attributeKey);
            default -> oAuth2UserInfo = null;
        }

        if (oAuth2UserInfo == null) throw new AuthException(ErrorCode.AUTHENTICATION_FAILED);
        return oAuth2UserInfo;
    }

}

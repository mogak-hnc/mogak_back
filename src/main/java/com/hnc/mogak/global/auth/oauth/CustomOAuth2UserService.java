package com.hnc.mogak.global.auth.oauth;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.oauth.userinfo.KakaoUserInfo;
import com.hnc.mogak.global.auth.oauth.userinfo.NaverUserInfo;
import com.hnc.mogak.global.auth.oauth.userinfo.OAuth2UserInfo;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.AuthException;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberId;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberPort memberPort;

    private static final String[] ADJECTIVES = {
            "집중하는", "성실한", "부지런한", "끈기있는", "열정적인", "차분한", "지적인", "노력하는", "똑똑한", "계획적인"
    };

    private static final String[] NOUNS = {
            "공부왕", "책벌레", "필기왕", "암기요정", "문제해결자", "수험생", "시험장인", "스터디리더", "지식탐험가", "집중마스터"
    };

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
        if (memberPort.existsByProviderId(providerId)) {
            return memberPort.loadMemberByProviderId(oAuth2UserInfo.getProviderId());
        }

        Member newMember = createNewMember(oAuth2UserInfo);
        Long newMemberId = memberPort.persist(newMember);

        return Member.withId(
                new MemberId(newMemberId),
                newMember.getMemberInfo(),
                newMember.getPlatformInfo(),
                newMember.getRole()
        );
    }

    private Member createNewMember(OAuth2UserInfo oAuth2UserInfo) {
        MemberInfo memberInfo = new MemberInfo(
                generateUniqueNickname(),
                oAuth2UserInfo.getEmail(),
                null,
                null,
                oAuth2UserInfo.getName(),
                false
        );

        PlatformInfo platformInfo = new PlatformInfo(
                oAuth2UserInfo.getProvider(),
                oAuth2UserInfo.getProviderId()
        );

        Role role = new Role(AuthConstant.ROLE_MEMBER);
        return Member.withoutId(memberInfo, platformInfo, role);
    }

    private String generateUniqueNickname() {
        Random random = new Random();

        String nickname = "";
        int attemptCount = 0;
        boolean isNicknameUnique = false;

        while (!isNicknameUnique && attemptCount < 10) {
            nickname = ADJECTIVES[random.nextInt(ADJECTIVES.length)] + " " + NOUNS[random.nextInt(NOUNS.length)];

            if (!memberPort.existsByNickname(nickname)) {
                isNicknameUnique = true;
            }

            attemptCount++;
        }

        if (attemptCount == 10) {
            nickname = UUID.randomUUID().toString();
        }

        return nickname;
    }

    private OAuth2UserInfo getOAuth2UserInfo(String provider, Map<String, Object> oAuth2UserAttributes, String attributeKey) {
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

package com.hnc.mogak.global.auth.oauth.userinfo;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;

import java.util.Map;
import java.util.UUID;

public class KakaoUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }


    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        if (attributes.containsKey("properties")) {
            Object propertiesObj = attributes.get("properties");
            if (propertiesObj instanceof Map) {
                Map<String, Object> properties = (Map<String, Object>) propertiesObj;
                if (properties.containsKey("nickname")) {
                    return properties.get("nickname").toString();
                }
            }
        }

        return UUID.randomUUID().toString();
    }

    @Override
    public String getNickname() {
        if (attributes.containsKey("properties")) {
            Object propertiesObj = attributes.get("properties");
            if (propertiesObj instanceof Map) {
                Map<String, Object> properties = (Map<String, Object>) propertiesObj;
                if (properties.containsKey("nickname")) {
                    return properties.get("nickname").toString();
                }
            }
        }
        return UUID.randomUUID().toString();
    }

    @Override
    public String getEmail() {
        if (attributes.containsKey("kakao_account")) {
            Object kakaoAccountObj = attributes.get("kakao_account");
            if (kakaoAccountObj instanceof Map) {
                Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoAccountObj;
                if (kakaoAccount.containsKey("email")) {
                    return kakaoAccount.get("email").toString();
                }
            }
        }
        return null;
    }

    @Override
    public Member mapToMember() {
        MemberInfo memberInfo = new MemberInfo(
                getNickname(),
                getEmail(),
                null,
                null,
                getName()
        );

        PlatformInfo platformInfo = new PlatformInfo(
                getProvider(),
                getProviderId()
        );

        Role role = new Role(AuthConstant.ROLE_MEMBER);
        return Member.withoutId(memberInfo, platformInfo, role);
    }
}

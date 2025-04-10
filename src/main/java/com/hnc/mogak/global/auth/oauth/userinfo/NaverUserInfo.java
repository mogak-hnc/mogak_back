package com.hnc.mogak.global.auth.oauth.userinfo;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

public class NaverUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> responseAttributes;

    public NaverUserInfo(Map<String, Object> attributes, String attributeKey) {
        this.responseAttributes = (Map<String, Object>) attributes.get(attributeKey);
    }

    @Override
    public String getProviderId() {
        return (String) responseAttributes.get("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return (String) responseAttributes.get("name");
    }

    @Override
    public String getNickname() {
        if (responseAttributes.containsKey("nickname")) {
            return responseAttributes.get("nickname").toString();
        } else {
            return UUID.randomUUID().toString();
        }
    }

    @Override
    public String getEmail() {
        return (String) responseAttributes.get("email");
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

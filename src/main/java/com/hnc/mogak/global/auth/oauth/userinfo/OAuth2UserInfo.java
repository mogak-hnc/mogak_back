package com.hnc.mogak.global.auth.oauth.userinfo;

import com.hnc.mogak.member.domain.Member;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getName();
    String getNickname();
    String getEmail();

    Member mapToMember();
}
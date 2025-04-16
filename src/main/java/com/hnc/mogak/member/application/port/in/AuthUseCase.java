package com.hnc.mogak.member.application.port.in;

import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginResponse;

public interface AuthUseCase {

    SocialLoginResponse handleSocialLogin(String provider, String providerId);

}

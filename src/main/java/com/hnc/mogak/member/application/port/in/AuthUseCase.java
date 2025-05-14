package com.hnc.mogak.member.application.port.in;

import com.hnc.mogak.member.adapter.in.web.dto.MemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthUseCase {

    SocialLoginResponse handleSocialLogin(String provider, String providerId);

    MemberInfoResponse getMemberInfo(Long memberId);

    Long deleteMember(Long memberId, String token);

    Long updateMemberInfo(Long memberId, String nickname, MultipartFile file, boolean deleteImage);

}

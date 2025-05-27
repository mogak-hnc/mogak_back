package com.hnc.mogak.member.application.port.in;

import com.hnc.mogak.member.adapter.in.web.dto.LoginResponse;
import com.hnc.mogak.member.adapter.in.web.dto.MemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.UpdateMemberInfoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AuthUseCase {

    LoginResponse handleSocialLogin(String provider, String providerId);

    LoginResponse loginAdmin(String id, String pw);

}

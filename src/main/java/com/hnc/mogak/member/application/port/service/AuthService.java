package com.hnc.mogak.member.application.port.service;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.cloud.S3Service;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MemberException;
import com.hnc.mogak.global.redis.RedisConstant;
import com.hnc.mogak.member.adapter.in.web.dto.AdminLoginRequest;
import com.hnc.mogak.member.adapter.in.web.dto.MemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.LoginResponse;
import com.hnc.mogak.member.adapter.in.web.dto.UpdateMemberInfoResponse;
import com.hnc.mogak.member.application.port.in.AuthUseCase;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import com.hnc.mogak.member.util.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final MemberPort memberPort;
    private final NicknameGenerator nicknameGenerator;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse loginAdmin(String id, String pw) {
        Member adminMember = memberPort.getAdminAccount(id, pw);
        String token = getToken(adminMember);

        return LoginResponse.builder()
                .memberId(adminMember.getMemberId().value())
                .token(token)
                .build();
    }

    @Override
    public LoginResponse handleSocialLogin(String provider, String providerId) {
        if (!memberPort.existsByProviderId(providerId)) {
            return createNewMember(provider, providerId);
        }

        Member findMember = memberPort.loadMemberByProviderId(providerId);
        return findMember.getMemberInfo().withdrawn()
                ? createNewMember(provider, providerId)
                : loginExistingMember(findMember);
    }

    private String getToken(Member member) {
        return jwtUtil.generateToken(
                String.valueOf(member.getMemberId().value()),
                member.getMemberInfo().nickname(),
                member.getRole().value()
        );
    }

    private LoginResponse createNewMember(String provider, String providerId) {
        MemberInfo memberInfo = new MemberInfo(
                nicknameGenerator.generate(),
                null,
                null,
                "Default",
                null,
                false,
                true
        );

        Member newMember = Member.withoutId(
                memberInfo,
                new PlatformInfo(provider, providerId),
                new Role(AuthConstant.ROLE_MEMBER)
        );

        Long memberId = memberPort.persist(newMember);
        newMember.assignId(memberId);

        return createLoginResponse(newMember);
    }

    private LoginResponse loginExistingMember(Member member) {
        return createLoginResponse(member);
    }

    private LoginResponse createLoginResponse(Member member) {
        return LoginResponse.builder()
                .memberId(member.getMemberId().value())
                .token(getToken(member))
                .build();
    }

}

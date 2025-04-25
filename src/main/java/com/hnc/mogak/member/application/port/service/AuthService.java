package com.hnc.mogak.member.application.port.service;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.member.adapter.in.web.dto.SocialLoginResponse;
import com.hnc.mogak.member.application.port.in.AuthUseCase;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.member.domain.vo.PlatformInfo;
import com.hnc.mogak.member.domain.vo.Role;
import com.hnc.mogak.member.util.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final MemberPort memberPort;
    private final NicknameGenerator nicknameGenerator;
    private final JwtUtil jwtUtil;

    @Override
    public SocialLoginResponse handleSocialLogin(String provider, String providerId) {
        if (memberPort.existsByProviderId(providerId)) {
            Member findMember = memberPort.loadMemberByProviderId(providerId);
            String token = getToken(findMember);

            return SocialLoginResponse.builder()
                    .memberId(findMember.getMemberId().value())
                    .token(token)
                    .build();
        }

        MemberInfo memberInfo = new MemberInfo(nicknameGenerator.generate(), null, null, null, null);
        PlatformInfo platformInfo = new PlatformInfo(provider, providerId);
        Role roleInfo = new Role(AuthConstant.ROLE_MEMBER);
        Member newMember = Member.withoutId(memberInfo, platformInfo, roleInfo);
        Long memberId = memberPort.persist(newMember);
        newMember.assignId(memberId);
        String token = getToken(newMember);

        return SocialLoginResponse.builder()
                .memberId(memberId)
                .token(token)
                .build();
    }

    private String getToken(Member member) {
        return jwtUtil.generateToken(
                String.valueOf(member.getMemberId().value()),
                member.getMemberInfo().nickname(),
                member.getRole().value()
        );
    }

}

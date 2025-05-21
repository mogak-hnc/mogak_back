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
    private final S3Service s3Service;
    private final NicknameGenerator nicknameGenerator;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
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
        if (memberPort.existsByProviderId(providerId)) {
            Member findMember = memberPort.loadMemberByProviderId(providerId);
            String token = getToken(findMember);

            return LoginResponse.builder()
                    .memberId(findMember.getMemberId().value())
                    .token(token)
                    .build();
        }

        MemberInfo memberInfo = new MemberInfo(nicknameGenerator.generate(), null, null, "Default", null, false, true);
        PlatformInfo platformInfo = new PlatformInfo(provider, providerId);
        Role roleInfo = new Role(AuthConstant.ROLE_MEMBER);
        Member newMember = Member.withoutId(memberInfo, platformInfo, roleInfo);
        Long memberId = memberPort.persist(newMember);
        newMember.assignId(memberId);
        String token = getToken(newMember);

        return LoginResponse.builder()
                .memberId(memberId)
                .token(token)
                .build();
    }

    @Override
    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberPort.loadMemberByMemberId(memberId);
        MemberInfo memberInfo = member.getMemberInfo();
        return new MemberInfoResponse(
                member.getMemberId().value(),
                memberInfo.imagePath(),
                memberInfo.nickname(),
                memberInfo.showBadge()
        );
    }

    @Override
    public Long deleteMember(Long memberId, String token) {
        memberPort.deleteMember(memberId);
        redisTemplate.opsForValue().set(
                RedisConstant.BLACK_LIST + token,
                "true",
                Duration.ofMillis(86400000)
        );

        // 웹소켓, 모각존 방장일 경우, 모각존 참여한 곳, 챌린지 방장일경우, 챌린지 참여한 곳
        return memberId;
    }

    @Override
    public UpdateMemberInfoResponse updateMemberInfo(Long memberId, String nickname, MultipartFile file, boolean deleteImage, boolean showBadge) {
        Member member = memberPort.loadMemberByMemberId(memberId);

        String updateImageUrl = member.getMemberInfo().imagePath();
        String updateNickname = member.getMemberInfo().nickname();
        boolean updateShowBadge = member.getMemberInfo().showBadge();

        if (deleteImage) {
            updateImageUrl = "Default";
        } else if (file != null && !file.isEmpty()) {
            updateImageUrl = s3Service.uploadImage(file, "member");
        }

        if (nickname != null && !nickname.trim().isEmpty()) {
            if (memberPort.existsByNickname(nickname)) {
                throw new MemberException(ErrorCode.NICKNAME_ALREADY_EXISTS);
            }

            updateNickname = nickname;
        }

        if (updateShowBadge != showBadge) {
            updateShowBadge = showBadge;
        }

        member.updateMemberInfo(updateImageUrl, updateNickname, updateShowBadge);
        Long savedMemberId = memberPort.persist(member);
        return new UpdateMemberInfoResponse(savedMemberId);
    }

    private String getToken(Member member) {
        return jwtUtil.generateToken(
                String.valueOf(member.getMemberId().value()),
                member.getMemberInfo().nickname(),
                member.getRole().value()
        );
    }

}

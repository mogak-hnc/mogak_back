package com.hnc.mogak.member.application.port.service;

import com.hnc.mogak.challenge.application.port.out.ChallengeQueryPort;
import com.hnc.mogak.global.cloud.S3PathConstants;
import com.hnc.mogak.global.cloud.S3Service;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.MemberException;
import com.hnc.mogak.global.redis.RedisConstant;
import com.hnc.mogak.member.adapter.in.web.dto.ChallengeInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.MemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.UpdateMemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.ZoneInfoResponse;
import com.hnc.mogak.member.application.port.in.ProfileUseCase;
import com.hnc.mogak.member.application.port.out.MemberPort;
import com.hnc.mogak.member.domain.Member;
import com.hnc.mogak.member.domain.vo.MemberInfo;
import com.hnc.mogak.zone.application.port.out.ZoneMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final MemberPort memberPort;
    private final ChallengeQueryPort challengeQueryPort;
    private final ZoneMemberPort zoneMemberPort;
    private final S3Service s3Service;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public MemberInfoResponse getProfile(Long requestMemberId, Long targetMemberId) {
        Member member = memberPort.loadMemberByMemberId(targetMemberId);
        return buildMemberInfoResponse(member);
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
            updateImageUrl = s3Service.uploadImage(file, S3PathConstants.MEMBER);
        }

        if (nickname != null && !nickname.trim().isEmpty()) {
            if (!nickname.equals(member.getMemberInfo().nickname())) {
                if (memberPort.existsByNickname(nickname)) {
                    throw new MemberException(ErrorCode.NICKNAME_ALREADY_EXISTS);
                }

                updateNickname = nickname;
            }
        }

        if (updateShowBadge != showBadge) {
            updateShowBadge = showBadge;
        }

        member.updateMemberInfo(updateImageUrl, updateNickname, updateShowBadge);
        Long savedMemberId = memberPort.persist(member);
        return new UpdateMemberInfoResponse(savedMemberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChallengeInfoResponse> findJoinedOngoingChallenges(Long memberId) {
        return challengeQueryPort.findJoinedOngoingChallenges(memberId);
    }

    @Override
    public List<ZoneInfoResponse> findJoinedZones(Long memberId) {
        return zoneMemberPort.findJoinedZones(memberId);
    }

    private MemberInfoResponse buildMemberInfoResponse(Member member) {
        MemberInfo memberInfo = member.getMemberInfo();

        return new MemberInfoResponse(
                member.getMemberId().value(),
                memberInfo.imagePath(),
                memberInfo.nickname(),
                memberInfo.showBadge()
        );
    }


}

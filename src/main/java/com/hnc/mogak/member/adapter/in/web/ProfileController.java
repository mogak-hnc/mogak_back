package com.hnc.mogak.member.adapter.in.web;

import com.hnc.mogak.badge.adapter.in.web.dto.GetBadgeResponse;
import com.hnc.mogak.badge.application.port.in.BadgeUseCase;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.member.adapter.in.web.dto.ChallengeInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.MemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.UpdateMemberInfoResponse;
import com.hnc.mogak.member.application.port.in.ProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/profile")
@Tag(name = "7. Profile", description = "프로필 관련 api")
public class ProfileController {

    private final ProfileUseCase profileUseCase;
    private final BadgeUseCase badgeUseCase;
    private final JwtUtil jwtUtil;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 반환합니다.")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfoResponse> getProfile(
            @RequestHeader(AuthConstant.AUTHORIZATION) String memberId,
            @PathVariable(value = "memberId") Long targetMemberId) {

        Long requestMemberId = Long.parseLong(jwtUtil.getMemberId(memberId));
        return ResponseEntity.status(HttpStatus.OK).body(profileUseCase.getProfile(requestMemberId, targetMemberId));
    }

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자를 탈퇴시킵니다.")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @DeleteMapping
    public ResponseEntity<Long> deleteMember (
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.status(HttpStatus.OK).body(profileUseCase.deleteMember(memberId, token));
    }

    @Operation(summary = "회원 수정", description = "로그인한 사용자의 정보를 수정합니다.")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UpdateMemberInfoResponse> updateMember(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "image", required = false) MultipartFile file,
            @RequestParam(value = "deleteImage", required = false, defaultValue = "false") boolean deleteImage,
            @RequestParam(value = "showBadge", required = false) boolean showBadge
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.ok(profileUseCase.updateMemberInfo(memberId, nickname, file, deleteImage, showBadge));
    }

    @Operation(summary = "참여중인 있는 챌린지 정보 조회", description = "참여 중인 챌린지 정보를 조회합니다.")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @GetMapping("/{memberId}/challenges")
    public ResponseEntity<List<ChallengeInfoResponse>> findJoinedOngoingChallenges(
            @PathVariable(value = "memberId") Long memberId
    ) {
        return ResponseEntity.ok(profileUseCase.findJoinedOngoingChallenges(memberId));
    }

    @Operation(summary = "뱃지 개인 조회", description = "현재 소유중인 뱃지를 조회합니다.")
    @GetMapping("/{targetMemberId}/badge")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<List<GetBadgeResponse>> getMemberBadge(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @PathVariable(value = "targetMemberId") Long targetMemberId) {
        Long requestMemberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.ok(badgeUseCase.getMemberBadge(requestMemberId, targetMemberId));
    }

}
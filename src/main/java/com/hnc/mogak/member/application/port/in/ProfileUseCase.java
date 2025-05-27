package com.hnc.mogak.member.application.port.in;

import com.hnc.mogak.member.adapter.in.web.dto.MemberInfoResponse;
import com.hnc.mogak.member.adapter.in.web.dto.UpdateMemberInfoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileUseCase {

    MemberInfoResponse getProfile(Long requestMemberId, Long targetMemberId);

    Long deleteMember(Long memberId, String token);

    UpdateMemberInfoResponse updateMemberInfo(Long memberId, String nickname, MultipartFile file, boolean deleteImage, boolean showBadge);

}

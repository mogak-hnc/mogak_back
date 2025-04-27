package com.hnc.mogak.zone.adapter.in.web.dto;

import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MogakZoneDetailResponse {

    private List<String> tagNames;
    private long hostMemberId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int joinedUserCount;
    private List<ZoneMemberInfo> zoneMemberInfoList;

    @Builder
    @Getter
    public static class ZoneMemberInfo {
        private Long memberId;
        private String nickname;
        private String imageUrl;
        private ZoneMemberStatus status;
    }

}


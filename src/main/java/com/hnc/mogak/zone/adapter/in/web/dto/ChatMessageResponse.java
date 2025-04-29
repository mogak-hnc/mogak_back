package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageResponse {

    private Long memberId;
    private Long mogakZoneId;
    private String nickname;
    private String imageUrl;
    private String message;
    private String now;

}
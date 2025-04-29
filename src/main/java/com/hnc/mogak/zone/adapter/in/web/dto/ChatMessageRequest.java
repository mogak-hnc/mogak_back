package com.hnc.mogak.zone.adapter.in.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequest {

    private Long memberId;
    private String message;

}

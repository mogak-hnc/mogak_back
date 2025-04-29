package com.hnc.mogak.zone.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SendChatMessageCommand {

    private Long memberId;
    private Long mogakZoneId;
    private String message;

}

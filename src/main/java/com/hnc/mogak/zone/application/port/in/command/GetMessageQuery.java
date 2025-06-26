package com.hnc.mogak.zone.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetMessageQuery {

    private Long mogakZoneId;
    private int page;
    private int size;

}

package com.hnc.mogak.zone.application.port.in.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class UpdateMogakZoneCommand {

    private Long mogakZoneId;
    private Long requestMemberId;
    private MultipartFile imageUrl;

}
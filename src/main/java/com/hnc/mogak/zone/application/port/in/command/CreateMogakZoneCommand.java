package com.hnc.mogak.zone.application.port.in.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Getter
public class CreateMogakZoneCommand {

    private String name;
    private int maxCapacity;
    private MultipartFile imageUrl;
    private String password;
    private boolean chatEnabled;
    private boolean passwordRequired;
    private Long memberId;
    private Set<String> tagNames;

}
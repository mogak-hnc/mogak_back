package com.hnc.mogak.challenge.application.port.in.command;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
@Getter
public class CreateArticleCommand {

    private Long memberId;
    private Long challengeId;
    private String description;
    private List<MultipartFile> images;

}

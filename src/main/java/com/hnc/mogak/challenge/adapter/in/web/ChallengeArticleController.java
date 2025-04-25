package com.hnc.mogak.challenge.adapter.in.web;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeArticleRequest;
import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeArticleResponse;
import com.hnc.mogak.challenge.application.port.in.ChallengeArticleUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateArticleCommand;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/mogak/challenge")
@RequiredArgsConstructor
public class ChallengeArticleController {

    private final ChallengeArticleUseCase challengeArticleUseCase;

    private final JwtUtil jwtUtil;

    @PostMapping(
            value = "/{challengeId}/verification",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            }
    )
    public ResponseEntity<CreateChallengeArticleResponse> create(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestPart("request") CreateChallengeArticleRequest request,
            @RequestPart("images") List<MultipartFile> images,
            @PathVariable(name = "challengeId") Long challengeId
    ) {
        long memberId = Long.parseLong(jwtUtil.getMemberId(token));

        CreateArticleCommand command = CreateArticleCommand.builder()
                .memberId(memberId)
                .challengeId(challengeId)
                .description(request.getDescription())
                .images(images)
                .build();

        Long articleId = challengeArticleUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateChallengeArticleResponse(articleId));
    }

}
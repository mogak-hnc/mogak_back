package com.hnc.mogak.challenge.adapter.in.web;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeArticleRequest;
import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeArticleResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleDetail;
import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleThumbNail;
import com.hnc.mogak.challenge.application.port.in.ChallengeArticleUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateArticleCommand;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/mogak/challenge")
@RequiredArgsConstructor
@Tag(name = "5. Challenge Article", description = "챌린지 인증 게시글 관련 API")
public class ChallengeArticleController {

    private final ChallengeArticleUseCase challengeArticleUseCase;

    private final JwtUtil jwtUtil;

    @Operation(summary = "챌린지 게시글 생성", description = "챌린지 게시글을 생성합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @PostMapping(
            value = "/{challengeId}/article/verification",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE} // multipart/form-data로 설정
    )
    public ResponseEntity<CreateChallengeArticleResponse> create(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,

            @Parameter(description = "챌린지 인증 게시글 정보", required = true)
            @RequestPart("request") CreateChallengeArticleRequest request,

            @Parameter(description = "챌린지 인증 이미지", required = true)
            @RequestPart("images") List<MultipartFile> images,

            @Parameter(
                    description = "챌린지 ID(챌린지를 먼저 생성하고 그 ID를 받아 써야됩니다.)",
                    required = true,
                    example = "1"
            )
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

    @Operation(summary = "챌린지 썸네일 게시글 조회", description = "챌린지 썸네일 게시글을 조회합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @GetMapping("/{challengeId}/article")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<Page<GetChallengeArticleThumbNail>> getChallengeArticlesThumbnail(
            @PathVariable(value = "challengeId") Long challengeId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(challengeArticleUseCase.getChallengeArticlesThumbnail(challengeId, page, size));
    }

    @Operation(summary = "챌린지 게시글 조회", description = "챌린지 썸네일 게시글을 조회합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @GetMapping("/{challengeId}/article/{articleId}")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<GetChallengeArticleDetail> getChallengeArticleDetail(
            @PathVariable(value = "challengeId") Long challengeId,
            @PathVariable(value = "articleId") Long articleId
    ) {

        return ResponseEntity.status(HttpStatus.OK).body(challengeArticleUseCase.getChallengeArticleDetail(challengeId, articleId));
    }

}
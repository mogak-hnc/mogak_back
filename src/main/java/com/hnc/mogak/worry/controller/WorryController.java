package com.hnc.mogak.worry.controller;

import com.hnc.mogak.global.PageResponse;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.worry.dto.*;
import com.hnc.mogak.worry.service.WorryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/worry")
@Tag(name = "6. Worry", description = "고민 생성 및 조회 API")
public class WorryController {

    private final WorryService worryService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "고민 생성", description = "새로운 고민을 생성합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)\n" +
            "duration에 ONE_MINUTE(실험용), ONE_HOUR, THREE_HOURS, SIX_HOURS, TWELVE_HOURS, TWENTY_FOUR_HOURS 넣으면 됩니다.")
    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<CreateWorryResponse> createWorry(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestBody CreateWorryRequest request
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.status(HttpStatus.CREATED).body(worryService.create(request, memberId));
    }

    @Operation(summary = "댓글 생성", description = "해당 고민에 댓글을 작성합니다.")
    @PostMapping("/{worryId}/comment")
    @PreAuthorize((AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN))
    public ResponseEntity<WorryCommentResponse> createComment(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestBody CreateWorryCommentRequest request,
            @Parameter(description = "댓글을 작성할 고민 ID") @PathVariable(value = "worryId") Integer worryId
    ) {
        Long memberId = Long.valueOf(jwtUtil.getMemberId(token));
        return ResponseEntity.status(HttpStatus.CREATED).body(worryService.createComment(request, memberId, worryId));
    }

    @Operation(summary = "고민 게시글 조회", description = "특정 고민의 게시글 정보를 조회합니다.")
    @GetMapping("/{worryId}")
    public ResponseEntity<WorryArticleResponse> getWorry(
            @Parameter(hidden = true) @RequestHeader(value = AuthConstant.AUTHORIZATION, required = false) String token,
            @Parameter(description = "조회할 고민 ID") @PathVariable Integer worryId
    ) {
        String memberId = (token == null) ? null : jwtUtil.getMemberId(token);
        return ResponseEntity.ok(worryService.getWorryArticle(worryId, memberId));
    }

    @Operation(summary = "고민 댓글 조회", description = "특정 고민의 댓글 정보를 조회합니다.")
    @GetMapping("/{worryId}/comments")
    public ResponseEntity<PageResponse<WorryCommentResponse>> getComments(
            @Parameter(description = "조회할 고민 ID") @PathVariable Integer worryId,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size

    ) {
        return ResponseEntity.ok(worryService.getWorryComments(worryId, page, size));
    }

    @Operation(summary = "고민 메인 페이지 조회", description = "메인 화면에서 표시할 고민 4개를 조회합니다.")
    @GetMapping
    public ResponseEntity<PageResponse<WorryPreview>> getWorryMainPage() {
        return ResponseEntity.ok(worryService.getWorryList("participant", 0, 4));
    }

    @Operation(summary = "고민 목록 조회", description = "고민 목록을 페이지네이션을 통해 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<PageResponse<WorryPreview>> getWorryList(
            @Parameter(description = "recent or empathy", example = "recent")
            @RequestParam(value = "sort", defaultValue = "recent") String sort,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(worryService.getWorryList(sort, page, size));
    }

    @Operation(summary = "공감 토글", description = "특정 고민에 공감을 토글합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @PostMapping("/{worryId}/empathy")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<WorryEmpathyResponse> toggleEmpathy(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Parameter(description = "공감할 고민 ID") @PathVariable(value = "worryId") Integer worryId
    ) {
        String memberId = jwtUtil.getMemberId(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(worryService.toggleEmpathy(worryId, memberId));
    }

    @Operation(summary = "고민 게시글 삭제", description = "특정 고민을 삭제합니다.")
    @DeleteMapping("/{worryId}")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<WorryArticleDeleteResponse> deleteWorryArticle(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Parameter(description = "삭제할 고민 ID") @PathVariable(value = "worryId") Integer worryId
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String role = jwtUtil.getRole(token);
        return ResponseEntity.status(HttpStatus.OK).body(worryService.deleteWorryArticle(worryId, memberId, role));
    }

    @Operation(summary = "고민 댓글 삭제", description = "특정 고민 댓글을 삭제합니다.")
    @DeleteMapping("/{worryId}/comments/{commentId}")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<WorryCommentDeleteResponse> deleteWorryComment(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Parameter(description = "삭제할 고민 ID") @PathVariable(value = "worryId") Integer worryId,
            @Parameter(description = "삭제할 고민 ID") @PathVariable(value = "commentId") Integer commentId
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String role = jwtUtil.getRole(token);
        return ResponseEntity.status(HttpStatus.OK).body(worryService.deleteWorryComment(worryId, memberId, commentId, role));
    }

}
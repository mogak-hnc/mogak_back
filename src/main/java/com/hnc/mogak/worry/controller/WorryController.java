package com.hnc.mogak.worry.controller;

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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/worry")
@Tag(name = "6. Worry", description = "고민 생성 및 조회 API")
public class WorryController {

    private final WorryService worryService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "고민 생성", description = "새로운 고민을 생성합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
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
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(hidden = true) @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestBody CreateWorryCommentRequest request,
            @Parameter(description = "댓글을 작성할 고민 ID") @PathVariable(value = "worryId") Integer worryId
    ) {
        String memberId = jwtUtil.getMemberId(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(worryService.createComment(request, memberId, worryId));
    }

    @Operation(summary = "고민 상세 조회", description = "특정 고민의 상세 정보를 조회합니다.")
    @GetMapping("/{worryId}")
    public ResponseEntity<WorryDetailResponse> getWorry(
            @Parameter(description = "조회할 고민 ID") @PathVariable Integer worryId
    ) {
        return ResponseEntity.ok(worryService.getWorry(worryId));
    }

    @Operation(summary = "고민 메인 페이지 조회", description = "메인 화면에서 표시할 고민 4개를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<WorryPreview>> getWorryMainPage() {
        return ResponseEntity.ok(worryService.getWorryList(0, 4));
    }

    @Operation(summary = "고민 목록 조회", description = "고민 목록을 페이지네이션을 통해 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<WorryPreview>> getWorryList(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        int start = page * size;
        int end = start + size - 1;

        return ResponseEntity.ok(worryService.getWorryList(start, end));
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

}
package com.hnc.mogak.worry.controller;

import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.worry.dto.*;
import com.hnc.mogak.worry.service.WorryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/worry")
public class WorryController {

    private final WorryService worryService;
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<CreateWorryResponse> createWorry(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestBody CreateWorryRequest request
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        return ResponseEntity.status(HttpStatus.CREATED).body(worryService.create(request, memberId));
    }

    @PostMapping("/{worryId}/comment")
    @PreAuthorize((AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN))
    public ResponseEntity<CommentResponse> createComment(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @RequestBody CreateWorryCommentRequest request,
            @PathVariable(value = "worryId") Integer worryId
    ) {
        String memberId = jwtUtil.getMemberId(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(worryService.createComment(request, memberId, worryId));
    }

    @GetMapping("/{worryId}")
    public ResponseEntity<WorryDetailResponse> getWorry(
            @PathVariable Integer worryId
    ) {
        return ResponseEntity.ok(worryService.getWorry(worryId));
    }

    @GetMapping
    public ResponseEntity<List<WorryPreview>> getWorryMainPage() {
        return ResponseEntity.ok(worryService.getWorryList(0, 4));
    }

    @GetMapping("/list")
    public ResponseEntity<List<WorryPreview>> getWorryList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        int start = page * size;
        int end = start + size - 1;

        return ResponseEntity.ok(worryService.getWorryList(start, end));
    }


    @PostMapping("/{worryId}/empathy")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<WorryEmpathyResponse> toggleEmpathy(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @PathVariable(value = "worryId") Integer worryId
    ) {
        String memberId = jwtUtil.getMemberId(token);
        return ResponseEntity.status(HttpStatus.CREATED).body(worryService.toggleEmpathy(worryId, memberId));
    }

}

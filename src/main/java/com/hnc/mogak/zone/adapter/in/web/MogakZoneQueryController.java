package com.hnc.mogak.zone.adapter.in.web;


import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.zone.adapter.in.web.dto.*;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.command.GetMessageQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/zone")
@Tag(name = "3. MogakZone", description = "모각존 생성 및 조회 API")
@Slf4j
public class MogakZoneQueryController {

    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;
    private final JwtUtil jwtUtil;

//    @Operation(summary = "모각존 메인 페이지 조회", description = "메인 화면에서 표시할 모각존 정보를 조회합니다.")
//    @GetMapping
//    public ResponseEntity<List<MogakZoneMainResponse>> getMogakZoneMainPage() {
//        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneQueryUseCase.getMainPage());
//    }

    @Operation(summary = "모각존 페이징 조회", description = "모각존 목록을 조회하며 필터링, 검색, 정렬 및 페이지네이션을 지원합니다.")
    @GetMapping
    public ResponseEntity<Page<MogakZoneSearchResponse>> searchMogakZone(
            @Parameter(description = "검색 태그 키워드")
            @RequestParam(value = "tag", required = false) String tag,

            @Parameter(description = "정렬 기준 (recent 또는 participant)")
            @RequestParam(value = "sort", required = false, defaultValue = "recent") String sort,

            @Parameter(description = "페이지 번호")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,

            @Parameter(description = "페이지 사이즈")
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        System.out.println("sort = " + sort);
        log.info("MogakZone Search Information");
        log.info("tag={}", tag);
        log.info("sort={}", sort);
        log.info("page={}", page);
        log.info("size={}", size);
        MogakZoneSearchQuery.Sort sortType = MogakZoneSearchQuery.Sort.valueOf(sort);
        log.info("sortType={}", sortType);
        MogakZoneSearchQuery mogakZoneSearchQuery = MogakZoneSearchQuery.builder()
                .tag(tag)
                .sort(sortType)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneQueryUseCase.searchMogakZone(mogakZoneSearchQuery));
    }

    @Operation(summary = "태그 목록 조회", description = "모각존에 관련된 태그 목록을 조회합니다.")
    @GetMapping("/tags")
    public ResponseEntity<List<TagNameResponse>> getTags() {
        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneQueryUseCase.getTagNames());
    }

    @Operation(summary = "모각존 디테일", description = "모각존 디테일 조회합니다.")
    @GetMapping("/{mogakZoneId}")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public MogakZoneDetailResponse getMogakZoneDetail(
            @Parameter(hidden = true)
            @RequestHeader(value = AuthConstant.AUTHORIZATION) String token,
            @PathVariable(value = "mogakZoneId") Long mogakZoneId
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        MogakZoneDetailQuery detailQuery = MogakZoneDetailQuery.builder()
                .mogakZoneId(mogakZoneId)
                .memberId(memberId)
                .build();

        return mogakZoneQueryUseCase.getDetail(detailQuery);
    }

    @GetMapping("/{mogakZoneId}/message")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<Page<ChatMessageResponse>> getMogakZoneMessage(
            @PathVariable(value = "mogakZoneId") Long mogakZoneId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "15") int size
    ) {
        GetMessageQuery query = GetMessageQuery.builder()
                .mogakZoneId(mogakZoneId)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.ok(mogakZoneQueryUseCase.getMessage(query));
    }

}
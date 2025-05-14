package com.hnc.mogak.zone.adapter.in.web;


import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneDetailResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.TagNameResponse;
import com.hnc.mogak.zone.application.port.in.MogakZoneQueryUseCase;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneDetailQuery;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mogak/zone")
@Tag(name = "3. MogakZone", description = "모각존 생성 및 조회 API")
public class MogakZoneQueryController {

    private final MogakZoneQueryUseCase mogakZoneQueryUseCase;

    @Operation(summary = "모각존 메인 페이지 조회", description = "메인 화면에서 표시할 모각존 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MogakZoneMainResponse>> getMogakZoneMainPage() {
        return ResponseEntity.status(HttpStatus.OK).body(mogakZoneQueryUseCase.getMainPage());
    }

    @Operation(summary = "모각존 목록 조회", description = "모각존 목록을 조회하며 필터링, 검색, 정렬 및 페이지네이션을 지원합니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<MogakZoneSearchResponse>> searchMogakZone(
            @Parameter(description = "검색 키워드 (모각존 이름 등)")
            @RequestParam(value = "search", required = false) String search,

            @Parameter(description = "필터링할 태그 목록")
            @RequestParam(value = "tag", required = false) String tag,

            @Parameter(description = "정렬 기준 (recent 또는 participant)")
            @RequestParam(value = "sort", required = false, defaultValue = "recent") String sort,

            @Parameter(description = "페이지 번호")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,

            @Parameter(description = "페이지 사이즈")
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        MogakZoneSearchQuery.Sort sortType = MogakZoneSearchQuery.Sort.valueOf(sort);
        MogakZoneSearchQuery mogakZoneSearchQuery = MogakZoneSearchQuery.builder()
                .search(search)
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
    @GetMapping("/detail/{mogakZoneId}")
    public MogakZoneDetailResponse getMogakZoneDetail(
            @PathVariable(value = "mogakZoneId") Long mogakZoneId
    ) {
        MogakZoneDetailQuery detailQuery = MogakZoneDetailQuery.builder()
                .mogakZoneId(mogakZoneId)
                .build();

        return mogakZoneQueryUseCase.getDetail(detailQuery);
    }
}

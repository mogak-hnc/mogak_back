package com.hnc.mogak.challenge.adapter.in.web;

import com.hnc.mogak.challenge.adapter.in.web.dto.*;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.util.mapper.DateParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/mogak/challenge")
@RequiredArgsConstructor
@Tag(name = "4. Challenge", description = "모각챌 관련 API")
public class ChallengeController {

    private final ChallengeUseCase challengeUseCase;
    private final JwtUtil jwtUtil;

    @Operation(summary = "챌린지 생성", description = "회원 또는 관리자가 챌린지를 생성합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<CreateChallengeResponse> createChallenge(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,

            @Parameter(
                    description = "챌린지 생성 요청 본문",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateChallengeRequest.class)
                    )
            )
            @Valid @RequestBody CreateChallengeRequest request
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String role = jwtUtil.getRole(token);
        LocalDate[] localDates = DateParser.parsePeriod(request.getPeriod());
        LocalDate startDate = localDates[0];
        LocalDate endDate = localDates[1];
        boolean isOfficial = role.equals(AuthConstant.ROLE_ADMIN);

        CreateChallengeCommand command = CreateChallengeCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(startDate)
                .endDate(endDate)
                .memberId(memberId)
                .official(isOfficial)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(challengeUseCase.create(command));
    }

    @Operation(summary = "챌린지 참여", description = "회원 또는 관리자가 챌린지에 참여합니다. (*우측 상단 Authorize 버튼에 Bearer를 제외한 토큰을 넣어주세요.)")
    @PostMapping("/{challengeId}/join")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<JoinChallengeResponse> joinChallenge(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,

            @Parameter(
                    description = "챌린지 ID (참여할 챌린지의 고유 ID)",
                    example = "1",
                    required = true
            )
            @PathVariable(name = "challengeId") Long challengeId
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        JoinChallengeCommand command = JoinChallengeCommand.builder()
                .memberId(memberId)
                .challengeId(challengeId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(challengeUseCase.join(command));
    }

    @Operation(summary = "챌린지 상세 조회", description = "챌린지 ID를 기반으로 상세 정보를 조회합니다.")
    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDetailResponse> getChallengeDetail(
            @Parameter(
                    description = "조회할 챌린지 ID",
                    example = "1",
                    required = true
            )
            @PathVariable(name = "challengeId") Long challengeId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(challengeUseCase.getDetail(challengeId));
    }

    @Operation(summary = "챌린지 메인 페이지", description = "메인 화면에 표시될 챌린지 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MogakChallengeMainResponse>> getMogakChallengeMainPage() {
        return ResponseEntity.status(HttpStatus.OK).body(challengeUseCase.getMainPage());
    }

    @Operation(summary = "챌린지 목록 조회", description = "챌린지 목록을 조회하며 필터링, 검색, 정렬 및 페이지네이션을 지원합니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<ChallengeSearchResponse>> searchChallenge(
            @Parameter(description = "검색 키워드 (모각존 이름 등)")
            @RequestParam(value = "search", required = false) String search,

            @Parameter(description = "공식 챌린지 (true 또는 false)")
            @RequestParam(value = "official", required = false, defaultValue = "false") boolean official,

            @Parameter(description = "정렬 기준 (recent 또는 participant)")
            @RequestParam(value = "sort", required = false, defaultValue = "recent") String sort,

            @Parameter(description = "페이지 번호")
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,

            @Parameter(description = "페이지 사이즈")
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {

        ChallengeSearchQuery.Sort sortType = ChallengeSearchQuery.Sort.valueOf(sort);

        ChallengeSearchQuery query = ChallengeSearchQuery.builder()
                .search(search)
                .official(official)
                .sort(sortType)
                .page(page)
                .size(size)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(challengeUseCase.searchChallenge(query));
    }

    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Long> deleteChallenge(
            @Parameter(hidden = true)
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Parameter(description = "삭제할 챌린지 ID")
            @PathVariable(name = "challengeId") Long challengeId
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String role = jwtUtil.getRole(token);
        return ResponseEntity.status(HttpStatus.OK).body(challengeUseCase.deleteChallenge(challengeId, memberId, role));
    }
}

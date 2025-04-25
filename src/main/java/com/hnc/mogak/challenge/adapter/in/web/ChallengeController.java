package com.hnc.mogak.challenge.adapter.in.web;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeDetailResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeRequest;
import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.adapter.in.web.dto.JoinChallengeResponse;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.application.port.in.command.JoinChallengeCommand;
import com.hnc.mogak.global.auth.AuthConstant;
import com.hnc.mogak.global.auth.jwt.JwtUtil;
import com.hnc.mogak.global.util.mapper.DateParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/mogak/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeUseCase challengeUseCase;
    private final JwtUtil jwtUtil;

    @PostMapping
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<CreateChallengeResponse> createChallenge(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Valid @RequestBody CreateChallengeRequest request
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        String role = jwtUtil.getRole(token);
        LocalDate[] localDates = DateParser.parsePeriod(request.getPeriod());
        LocalDate startDate = localDates[0];
        LocalDate endDate = localDates[1];
        boolean isOfficial = role.equals(AuthConstant.ROLE_ADMIN);

        CreateChallengeCommand command =  CreateChallengeCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(startDate)
                .endDate(endDate)
                .memberId(memberId)
                .official(isOfficial)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(challengeUseCase.create(command));
    }

    @PostMapping("/{challengeId}/join")
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER_OR_ADMIN)
    public ResponseEntity<JoinChallengeResponse> joinChallenge(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @PathVariable(name = "challengeId") Long challengeId
    ) {
        Long memberId = Long.parseLong(jwtUtil.getMemberId(token));
        JoinChallengeCommand command = JoinChallengeCommand.builder()
                .memberId(memberId)
                .challengeId(challengeId)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(challengeUseCase.join(command));
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<ChallengeDetailResponse> getChallengeDetail(
            @PathVariable(name = "challengeId") Long challengeId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(challengeUseCase.getDetail(challengeId));
    }

}

package com.hnc.mogak.challenge.adapter.in.web;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeRequest;
import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.application.port.in.ChallengeUseCase;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
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
    @PreAuthorize(AuthConstant.ACCESS_ONLY_MEMBER)
    public ResponseEntity<CreateChallengeResponse> createChallenge(
            @RequestHeader(AuthConstant.AUTHORIZATION) String token,
            @Valid @RequestBody CreateChallengeRequest request
    ) {
        String memberId = jwtUtil.getMemberId(token);
        LocalDate[] localDates = DateParser.parsePeriod(request.getPeriod());
        LocalDate startDate = localDates[0];
        LocalDate endDate = localDates[1];

        CreateChallengeCommand command =  CreateChallengeCommand.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(startDate)
                .endDate(endDate)
                .memberId(Long.parseLong(memberId))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(challengeUseCase.create(command));
    }

}

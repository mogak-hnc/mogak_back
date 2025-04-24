package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.domain.Challenge;
import com.hnc.mogak.challenge.domain.vo.ChallengeDuration;
import com.hnc.mogak.challenge.domain.vo.ChallengeId;
import com.hnc.mogak.challenge.domain.vo.Content;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ChallengeMapper {

    public ChallengeEntity mapToJpaEntity(Challenge challenge) {
        Long challengeId = challenge.getChallengeId() != null ? challenge.getChallengeId().value() : null;
        String title = challenge.getContent().title();
        String description = challenge.getContent().description();
        boolean official = challenge.getContent().official();
            LocalDate startDate = challenge.getChallengeDuration().startDate();
        LocalDate endDate = challenge.getChallengeDuration().endDate();

        return ChallengeEntity.builder()
                .id(challengeId)
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .official(official)
                .build();
    }

    public Challenge mapToDomain(ChallengeEntity challengeEntity) {
        ChallengeId challengeId = new ChallengeId(challengeEntity.getId());
        Content content = new Content(
                challengeEntity.getTitle(),
                challengeEntity.getDescription(),
                challengeEntity.isOfficial()
        );
        ChallengeDuration challengeDuration =
                new ChallengeDuration(
                        challengeEntity.getStartDate(),
                        challengeEntity.getEndDate()
                );

        return Challenge.withId(challengeId, content, challengeDuration);
    }

    public Challenge mapToDomain(CreateChallengeCommand command) {
        Content content = new Content(
                command.getTitle(),
                command.getDescription(),
                command.isOfficial()
        );
        ChallengeDuration challengeDuration =
                new ChallengeDuration(
                        command.getStartDate(),
                        command.getEndDate()
                );

        return Challenge.withoutId(content, challengeDuration);
    }

    public CreateChallengeResponse mapToChallengeResponse(ChallengeEntity savedChallengeEntity, MemberEntity memberEntity) {
        return CreateChallengeResponse.builder()
                .challengeId(savedChallengeEntity.getId())
                .title(savedChallengeEntity.getTitle())
                .description(savedChallengeEntity.getDescription())
                .startDate(savedChallengeEntity.getStartDate())
                .endDate(savedChallengeEntity.getEndDate())
                .creatorMemberId(memberEntity.getId())
                .official(savedChallengeEntity.isOfficial())
                .build();
    }

}
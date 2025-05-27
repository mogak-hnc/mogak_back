package com.hnc.mogak.global.util.mapper;

import com.hnc.mogak.challenge.adapter.in.web.dto.CreateChallengeResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import com.hnc.mogak.challenge.application.port.in.command.CreateChallengeCommand;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.challenge.domain.challenge.vo.ChallengeDuration;
import com.hnc.mogak.challenge.domain.challenge.vo.ChallengeId;
import com.hnc.mogak.challenge.domain.challenge.vo.Content;
import com.hnc.mogak.challenge.domain.challenge.vo.ExtraDetails;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ChallengeMapper {

    public static ChallengeEntity mapToJpaEntity(Challenge challenge) {
        Long challengeId = challenge.getChallengeId() != null ? challenge.getChallengeId().value() : null;
        String title = challenge.getContent().title();
        String description = challenge.getContent().description();
        boolean official = challenge.getExtraDetails().official();
        int totalParticipants = challenge.getExtraDetails().totalParticipants();
        LocalDate startDate = challenge.getChallengeDuration().startDate();
        LocalDate endDate = challenge.getChallengeDuration().endDate();

        return ChallengeEntity.builder()
                .id(challengeId)
                .title(title)
                .description(description)
                .totalParticipants(totalParticipants)
                .startDate(startDate)
                .endDate(endDate)
                .official(official)
                .status(challenge.getStatus())
                .build();
    }

    public static Challenge mapToDomain(ChallengeEntity challengeEntity) {
        ChallengeId challengeId = new ChallengeId(challengeEntity.getId());
        Content content = new Content(
                challengeEntity.getTitle(),
                challengeEntity.getDescription()
        );

        ExtraDetails extraDetails = new ExtraDetails(
                challengeEntity.isOfficial(),
                challengeEntity.getTotalParticipants()
        );
        ChallengeDuration challengeDuration =
                new ChallengeDuration(
                        challengeEntity.getStartDate(),
                        challengeEntity.getEndDate()
                );

        return Challenge.withId(challengeId, content, extraDetails, challengeDuration, challengeEntity.getStatus());
    }

    public static Challenge mapToDomain(CreateChallengeCommand command) {
        Content content = new Content(
                command.getTitle(),
                command.getDescription()
        );

        ExtraDetails extraDetails = new ExtraDetails(command.isOfficial(), 0);
        ChallengeDuration challengeDuration =
                new ChallengeDuration(
                        command.getStartDate(),
                        command.getEndDate()
                );

        return Challenge.withoutId(content, extraDetails, challengeDuration, ChallengeStatus.BEFORE);
    }

    public static CreateChallengeResponse mapToChallengeResponse(ChallengeEntity savedChallengeEntity, MemberEntity memberEntity) {
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
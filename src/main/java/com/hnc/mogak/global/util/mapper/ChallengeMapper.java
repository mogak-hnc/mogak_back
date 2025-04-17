package com.hnc.mogak.global.util.mapper;

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

    public ChallengeEntity mapToJpaEntity(Challenge challenge, MemberEntity memberEntity) {
        Long challengeId = challenge.getChallengeId() != null ? challenge.getChallengeId().value() : null;
        String title = challenge.getContent().title();
        String description = challenge.getContent().description();
        LocalDate startDate = challenge.getChallengeDuration().startDate();
        LocalDate endDate = challenge.getChallengeDuration().endDate();

        return ChallengeEntity.builder()
                .challengeId(challengeId)
                .title(title)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .memberEntity(memberEntity)
                .build();
    }

    public Challenge mapToDomain(ChallengeEntity challengeEntity, Member challengeCreator) {
        ChallengeId challengeId = new ChallengeId(challengeEntity.getChallengeId());
        Content content = new Content(
                challengeEntity.getTitle(),
                challengeEntity.getDescription()
        );
        ChallengeDuration challengeDuration =
                new ChallengeDuration(
                        challengeEntity.getStartDate(),
                        challengeEntity.getEndDate()
                );

        return Challenge.withId(challengeId, content, challengeDuration, challengeCreator);
    }

    public Challenge mapToDomain(CreateChallengeCommand command, Member challengeCreator) {
        ChallengeId challengeId = new ChallengeId(null);
        Content content = new Content(
                command.getTitle(),
                command.getDescription()
        );
        ChallengeDuration challengeDuration =
                new ChallengeDuration(
                        command.getStartDate(),
                        command.getEndDate()
                );

        return Challenge.withId(challengeId, content, challengeDuration, challengeCreator);
    }

}
package com.hnc.mogak.challenge.domain.challenge;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeStatus;
import com.hnc.mogak.challenge.domain.challenge.vo.ChallengeDuration;
import com.hnc.mogak.challenge.domain.challenge.vo.ChallengeId;
import com.hnc.mogak.challenge.domain.challenge.vo.Content;
import com.hnc.mogak.challenge.domain.challenge.vo.ExtraDetails;
import com.hnc.mogak.member.domain.Member;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Challenge {

    private ChallengeId challengeId;
    private Content content;
    private ExtraDetails extraDetails;
    private ChallengeDuration challengeDuration;
    private ChallengeStatus status;

    public static Challenge withId(
            ChallengeId challengeId,
            Content content,
            ExtraDetails extraDetails,
            ChallengeDuration challengeDuration,
            ChallengeStatus status) {
        return new Challenge(challengeId, content, extraDetails, challengeDuration, status);
    }

    public static Challenge withoutId(
            Content content,
            ExtraDetails extraDetails,
            ChallengeDuration challengeDuration,
            ChallengeStatus status) {

        return new Challenge(null, content, extraDetails, challengeDuration, status);
    }

    public boolean isAlreadyJoin(Member member, List<Member> members) {
        for (Member m : members) {
            if (Objects.equals(member.getMemberId().value(), m.getMemberId().value())) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlreadyStart() {
        return this.status.equals(ChallengeStatus.ONGOING) || this.status.equals(ChallengeStatus.COMPLETED);
    }

    public boolean isCreator(Long memberId, Long memberOwnerId) {
        return Objects.equals(memberId, memberOwnerId);
    }

    public void increaseParticipantCount() {
        this.extraDetails = new ExtraDetails(extraDetails.official(), extraDetails.totalParticipants() + 1);
    }

}

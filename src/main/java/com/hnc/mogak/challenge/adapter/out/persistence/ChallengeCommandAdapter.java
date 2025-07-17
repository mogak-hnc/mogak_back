package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import com.hnc.mogak.badge.adapter.out.persistence.repository.BadgeRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeBadgeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeOwnerEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.*;
import com.hnc.mogak.challenge.application.port.out.ChallengeCommandPort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.BadgeException;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChallengeCommandAdapter implements ChallengeCommandPort {

    private final ChallengeRepository challengeRepository;
    private final ChallengeOwnerRepository challengeOwnerRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeArticleRepository challengeArticleRepository;
    private final ChallengeBadgeRepository challengeBadgeRepository;
    private final BadgeRepository badgeRepository;

    @Override
    public Challenge persist(Challenge challenge) {
        ChallengeEntity savedChallengeEntity = challengeRepository.save(ChallengeMapper.mapToJpaEntity(challenge));
        return ChallengeMapper.mapToDomain(savedChallengeEntity);
    }

    @Override
    public void deleteChallenge(Challenge challenge) {
        ChallengeEntity challengeEntity = ChallengeMapper.mapToJpaEntity(challenge);
        deleteChallengeWithAssociations(challengeEntity);
    }

    @Override
    public void saveChallengeBadge(Challenge challenge, Long badgeId) {
        ChallengeEntity challengeEntity = ChallengeMapper.mapToJpaEntity(challenge);
        BadgeEntity badgeEntity = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new BadgeException(ErrorCode.NOT_EXISTS_BADGE));

        challengeBadgeRepository.save(buildChallengeBadgeEntity(badgeEntity, challengeEntity));
    }

    @Override
    public void saveChallengeOwner(Member member, Challenge challenge) {
        challengeOwnerRepository.save(
                ChallengeOwnerEntity.builder()
                        .challengeEntity(ChallengeMapper.mapToJpaEntity(challenge))
                        .memberEntity(MemberMapper.mapToJpaEntity(member))
                        .build()
        );
    }

    private void deleteChallengeWithAssociations(ChallengeEntity challengeEntity) {
        challengeRepository.delete(challengeEntity);
        challengeOwnerRepository.deleteByChallengeEntity(challengeEntity);
        challengeMemberRepository.deleteAllByChallengeEntity(challengeEntity);
        challengeArticleRepository.deleteByChallengeEntity(challengeEntity);
    }

    private ChallengeBadgeEntity buildChallengeBadgeEntity(BadgeEntity badgeEntity, ChallengeEntity challengeEntity) {
        return ChallengeBadgeEntity.builder()
                .badgeEntity(badgeEntity)
                .challengeEntity(challengeEntity)
                .build();
    }

}

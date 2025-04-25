package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeArticleEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeImageEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeArticleRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeImageRepository;
import com.hnc.mogak.challenge.application.port.out.ChallengeArticlePort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeArticleAdapter implements ChallengeArticlePort {

    private final ChallengeArticleRepository challengeArticleRepository;
    private final ChallengeImageRepository challengeImageRepository;

    @Override
    public Long persist(Member member, Challenge challenge, List<String> imageUrls, String description) {
        ChallengeEntity challengeEntity = ChallengeMapper.mapToJpaEntity(challenge);

        ChallengeArticleEntity entity = challengeArticleRepository.save(ChallengeArticleEntity.builder()
                .description(description)
                .challengeEntity(challengeEntity)
                .build());

        imageUrls.forEach(imageUrl -> {
            ChallengeImageEntity imageEntity = createImageEntity(entity, imageUrl);
            challengeImageRepository.save(imageEntity);
            entity.getChallengeImageEntityList().add(imageEntity);
        });

        return challenge.getChallengeId().value();
    }

    @Override
    public List<ChallengeArticleEntity> findImagesByChallengeId(Long challengeId) {
        return challengeArticleRepository.getChallengeArticleListByChallengeId(challengeId);
    }

    private ChallengeImageEntity createImageEntity(ChallengeArticleEntity entity, String imageUrl) {
        return ChallengeImageEntity.builder()
                .challengeArticleEntity(entity)
                .imageUrl(imageUrl)
                .build();
    }

}

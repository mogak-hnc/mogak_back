package com.hnc.mogak.challenge.adapter.out.persistence;

import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleDetail;
import com.hnc.mogak.challenge.adapter.in.web.dto.GetChallengeArticleThumbNail;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeArticleEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.ChallengeImageEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.projection.GetChallengeArticleThumbNailProjection;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeArticleRepository;
import com.hnc.mogak.challenge.adapter.out.persistence.repository.ChallengeImageRepository;
import com.hnc.mogak.challenge.application.port.out.ChallengeArticlePort;
import com.hnc.mogak.challenge.domain.challenge.Challenge;
import com.hnc.mogak.global.exception.ErrorCode;
import com.hnc.mogak.global.exception.exceptions.ChallengeArticleException;
import com.hnc.mogak.global.util.mapper.ChallengeMapper;
import com.hnc.mogak.global.util.mapper.MemberMapper;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChallengeArticleAdapter implements ChallengeArticlePort {

    private final ChallengeArticleRepository challengeArticleRepository;
    private final ChallengeImageRepository challengeImageRepository;

    @Override
    public Long persist(Member member, Challenge challenge, List<String> imageUrls, String description) {
        ChallengeEntity challengeEntity = ChallengeMapper.mapToJpaEntity(challenge);
        MemberEntity memberEntity = MemberMapper.mapToJpaEntity(member);

        ChallengeArticleEntity entity = challengeArticleRepository.save(ChallengeArticleEntity.builder()
                .description(description)
                .challengeEntity(challengeEntity)
                .memberEntity(memberEntity)
                .thumbnailUrl(imageUrls.get(0))
                .build());

        imageUrls.forEach(imageUrl -> {
            ChallengeImageEntity imageEntity = createImageEntity(entity, imageUrl);
            challengeImageRepository.save(imageEntity);
            entity.getChallengeImageEntityList().add(imageEntity);
        });

        return entity.getId();
    }

    @Override
    public Page<GetChallengeArticleThumbNail> getChallengeArticlesThumbnail(Long challengeId, Pageable pageable) {
        Page<GetChallengeArticleThumbNailProjection> articleProjections =
                challengeArticleRepository.getChallengeArticlesByChallengeId(challengeId, pageable);

        if (articleProjections.isEmpty()) return Page.empty(pageable);

        List<GetChallengeArticleThumbNail> challengeArticleThumbNails = articleProjections.stream()
                .map(projection -> GetChallengeArticleThumbNail.builder()
                        .memberId(projection.getMemberId())
                        .challengeArticleId(projection.getChallengeArticleId())
                        .thumbnailUrl(projection.getThumbnailUrl())
                        .build())
                .toList();

        return new PageImpl<>(challengeArticleThumbNails, pageable, articleProjections.getTotalElements());
    }

    @Override
    public GetChallengeArticleDetail findChallengeArticle(Long challengeId, Long articleId) {
        ChallengeArticleEntity entity = challengeArticleRepository.findByChallengeIdAndArticleId(challengeId, articleId);

        if (entity == null) throw new ChallengeArticleException(ErrorCode.INVALID_ACCESS);

        List<String> imageUrl = entity.getChallengeImageEntityList().stream()
                .map(ChallengeImageEntity::getImageUrl)
                .toList();

        return GetChallengeArticleDetail.builder()
                .challengeArticleId(articleId)
                .description(entity.getDescription())
                .challengeId(challengeId)
                .imageUrl(imageUrl)
                .memberId(entity.getMemberEntity().getId())
                .nickname(entity.getMemberEntity().getNickname())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public boolean isAlreadyPostToday(Long challengeId, Long memberId) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        return challengeArticleRepository.isAlreadyPostToday(challengeId, memberId, startOfDay, endOfDay);
    }

    private ChallengeImageEntity createImageEntity(ChallengeArticleEntity entity, String imageUrl) {
        return ChallengeImageEntity.builder()
                .challengeArticleEntity(entity)
                .imageUrl(imageUrl)
                .build();
    }

}

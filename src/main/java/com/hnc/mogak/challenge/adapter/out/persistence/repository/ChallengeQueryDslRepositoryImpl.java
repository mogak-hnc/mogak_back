package com.hnc.mogak.challenge.adapter.out.persistence.repository;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.QChallengeEntity;
import com.hnc.mogak.challenge.adapter.out.persistence.entity.QChallengeMemberEntity;
import com.hnc.mogak.challenge.application.port.in.query.ChallengeSearchQuery;
import com.hnc.mogak.member.adapter.out.persistence.QMemberEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class ChallengeQueryDslRepositoryImpl implements ChallengeQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ChallengeSearchResponse> searchChallenge(ChallengeSearchQuery query, Pageable pageable) {
        QChallengeEntity challenge = QChallengeEntity.challengeEntity;
        QMemberEntity member = QMemberEntity.memberEntity;
        QChallengeMemberEntity challengeMember = QChallengeMemberEntity.challengeMemberEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (query.getSearch() != null && !query.getSearch().isBlank()) {
            builder.and(challenge.title.containsIgnoreCase(query.getSearch()));
        }

        if (query.getOfficial() == null) {
            builder.and(challenge.official.eq(true).or(challenge.official.eq(false)));
        } else {
            builder.and(challenge.official.eq(query.getOfficial()));
        }

        if (query.getStatus() != null) {
            builder.and(challenge.status.eq(query.getStatus()));
        }

        List<Tuple> challengeInfos = queryFactory
                .select(challenge.id, challenge.official, challenge.title, challenge.startDate, challenge.endDate, challenge.status)
                .from(challenge)
                .leftJoin(challengeMember).on(challengeMember.challengeEntity.eq(challenge))
                .where(builder)
                .groupBy(challenge.id)
                .orderBy(getSort(query))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> ids = challengeInfos.stream().map(tuple -> tuple.get(challenge.id)).toList();

        List<Tuple> memberTuples = queryFactory
                .select(challengeMember.challengeEntity.id, member.imagePath)
                .from(challengeMember)
                .join(member).on(challengeMember.memberEntity.eq(member))
                .where(challengeMember.challengeEntity.id.in(ids))
                .fetch();

        Map<Long, List<String>> challengeIdToImages = new HashMap<>();
        for (Tuple tuple : memberTuples) {
            Long challengeId = tuple.get(challengeMember.challengeEntity.id);
            String imagePath = tuple.get(member.imagePath);
            List<String> imageList = challengeIdToImages.computeIfAbsent(challengeId, k -> new ArrayList<>());

            if (imageList.size() <= 3) {
                if (imagePath == null) {
                    imagePath = "Defult";
                }

                imageList.add(imagePath);
            }
        }

        List<ChallengeSearchResponse> result = challengeInfos.stream()
                .map(tuple -> {
                    Long challengeId = tuple.get(challenge.id);
                    String title = tuple.get(challenge.title);
                    LocalDate start = tuple.get(challenge.startDate);
                    LocalDate end = tuple.get(challenge.endDate);
                    Boolean official = tuple.get(challenge.official);
                    List<String> images = challengeIdToImages.getOrDefault(challengeId, List.of());

                    return ChallengeSearchResponse.builder()
                            .challengeId(challengeId)
                            .official(official)
                            .title(title)
                            .startDate(start)
                            .endDate(end)
                            .memberImageUrl(images)
                            .status(tuple.get(challenge.status))
                            .build();
                })
                .toList();

        Long total = queryFactory
                .select(challenge.countDistinct())
                .from(challenge)
                .where(builder)
                .fetchOne();

        long safeTotal = Optional.ofNullable(total).orElse(0L);
        return new PageImpl<>(result, pageable, safeTotal);
    }

    private OrderSpecifier<?> getSort(ChallengeSearchQuery query) {
        if (query.getSort().equals(ChallengeSearchQuery.Sort.participant)) {
            return QChallengeMemberEntity.challengeMemberEntity.id.count().desc();
        } else {
            return QChallengeEntity.challengeEntity.modifiedAt.desc();
        }
    }

}

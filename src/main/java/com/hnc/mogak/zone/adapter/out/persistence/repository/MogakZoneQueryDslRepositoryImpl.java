package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.member.adapter.out.persistence.QMemberEntity;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.*;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MogakZoneQueryDslRepositoryImpl implements MogakZoneQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Page<MogakZoneSearchResponse> findMogakZone(MogakZoneSearchQuery query, Pageable pageable) {
        final int MAX_PAGE_LIMIT = 20;
        final long maxCountLimit = (long) pageable.getPageSize() * MAX_PAGE_LIMIT;

        QMogakZoneEntity mogakZone = QMogakZoneEntity.mogakZoneEntity;
        QZoneTagEntity zoneTag = QZoneTagEntity.zoneTagEntity;
        QTagEntity tag = QTagEntity.tagEntity;
        QZoneMemberEntity zoneMember = QZoneMemberEntity.zoneMemberEntity;
        QMemberEntity member = QMemberEntity.memberEntity;
        QZoneSummary summary = QZoneSummary.zoneSummary;

        if (query.getTag() != null && !query.getTag().isBlank()) {
            // 🔍 태그 조건이 있을 경우
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(tag.name.eq(query.getTag()));

            // 1단계: zoneId 페이징
            List<Long> zoneIds = queryFactory
                    .select(mogakZone.id)
                    .from(mogakZone)
                    .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
                    .leftJoin(tag).on(zoneTag.tag.eq(tag))
                    .where(builder)
                    .groupBy(mogakZone.id)
                    .orderBy(getSortFromZone(query), mogakZone.id.asc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            if (zoneIds.isEmpty()) return Page.empty(pageable);

            // 2단계: 상세 정보 조회
            List<Tuple> zoneInfos = queryFactory
                    .select(mogakZone.id, mogakZone.name, mogakZone.passwordRequired)
                    .from(mogakZone)
                    .where(mogakZone.id.in(zoneIds))
                    .fetch();

            // 태그 조회
            Map<Long, List<String>> zoneIdToTags = queryFactory
                    .select(zoneTag.zone.id, tag.name)
                    .from(zoneTag)
                    .join(tag).on(zoneTag.tag.eq(tag))
                    .where(zoneTag.zone.id.in(zoneIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                            tuple -> tuple.get(zoneTag.zone.id),
                            Collectors.mapping(t -> t.get(tag.name), Collectors.toList())
                    ));

            // 이미지 조회
            Map<Long, List<String>> zoneIdToImages = queryFactory
                    .select(zoneMember.mogakZoneEntity.id, member.imagePath)
                    .from(zoneMember)
                    .join(member).on(zoneMember.memberEntity.eq(member))
                    .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                            tuple -> tuple.get(zoneMember.mogakZoneEntity.id),
                            Collectors.mapping(t -> {
                                String img = t.get(member.imagePath);
                                return img == null ? "Default" : img;
                            }, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().limit(3).toList()))
                    ));

            // 결과 조합
            List<MogakZoneSearchResponse> content = zoneInfos.stream()
                    .map(tuple -> {
                        Long id = tuple.get(mogakZone.id);
                        return MogakZoneSearchResponse.builder()
                                .mogakZoneId(id)
                                .name(tuple.get(mogakZone.name))
                                .passwordRequired(Boolean.TRUE.equals(tuple.get(mogakZone.passwordRequired)))
                                .tagNames(zoneIdToTags.getOrDefault(id, List.of()))
                                .memberImageUrls(zoneIdToImages.getOrDefault(id, List.of()))
                                .build();
                    }).toList();

            // count
            List<Long> countCheckList = queryFactory
                    .select(summary.id)
                    .from(summary)
                    .limit(maxCountLimit + 1)
                    .fetch();

            long totalCount = pageable.getOffset() + countCheckList.size();

            return new PageImpl<>(content, pageable, totalCount);
//            Long total = queryFactory
//                    .select(mogakZone.id.countDistinct())
//                    .from(mogakZone)
//                    .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
//                    .leftJoin(tag).on(zoneTag.tag.eq(tag))
//                    .where(builder)
//                    .fetchOne();
//
//            return new PageImpl<>(content, pageable, total == null ? 0 : total);
        } else {
            List<Tuple> zoneInfos = queryFactory
                    .select(summary.id, summary.name, summary.passwordRequired)
                    .from(summary)
                    .orderBy(getSortFromSummary(query), summary.id.asc()) // 안정성 있는 정렬
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            if (zoneInfos.isEmpty()) return Page.empty(pageable);

            // 2단계: 상세 정보 조회
            List<Long> zoneIds = zoneInfos.stream().map(tuple -> tuple.get(summary.id)).toList();

            // 3단계: 태그 조회
            Map<Long, List<String>> zoneIdToTags = queryFactory
                    .select(zoneTag.zone.id, tag.name)
                    .from(zoneTag)
                    .join(tag).on(zoneTag.tag.eq(tag))
                    .where(zoneTag.zone.id.in(zoneIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                            tuple -> tuple.get(zoneTag.zone.id),
                            Collectors.mapping(t -> t.get(tag.name), Collectors.toList())
                    ));

            // 4단계: 멤버 이미지 조회 (최대 3개 제한)
            Map<Long, List<String>> zoneIdToImages = queryFactory
                    .select(zoneMember.mogakZoneEntity.id, member.imagePath)
                    .from(zoneMember)
                    .join(member).on(zoneMember.memberEntity.eq(member))
                    .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(
                            tuple -> tuple.get(zoneMember.mogakZoneEntity.id),
                            Collectors.mapping(t -> {
                                String img = t.get(member.imagePath);
                                return img == null ? "Default" : img;
                            }, Collectors.collectingAndThen(Collectors.toList(), list -> list.stream().limit(3).toList()))
                    ));



            // 5단계: 응답 DTO 조립
            List<MogakZoneSearchResponse> content = zoneInfos.stream()
                    .map(tuple -> {
                        Long zoneId = tuple.get(summary.id);
                        return MogakZoneSearchResponse.builder()
                                .mogakZoneId(zoneId)
                                .name(tuple.get(summary.name))
                                .passwordRequired(Boolean.TRUE.equals(tuple.get(summary.passwordRequired)))
                                .tagNames(zoneIdToTags.getOrDefault(zoneId, List.of()))
                                .memberImageUrls(zoneIdToImages.getOrDefault(zoneId, List.of()))
                                .build();
                    })
                    .toList();

            // 6단계: 전체 개수 카운트
//            Object cached = redisTemplate.opsForValue().get(RedisConstant.ZONE_SUMMARY_TOTAL_COUNT);
//
//            Long totalCount;
//            if (cached == null) {
//                totalCount = queryFactory
//                        .select(summary.count())
//                        .from(summary)
//                        .fetchOne();
//
//                redisTemplate.opsForValue().set(RedisConstant.ZONE_SUMMARY_TOTAL_COUNT, totalCount, Duration.ofMinutes(10));
//            } else {
//                totalCount = ((Number) cached).longValue();
//            }
//
//            return new PageImpl<>(content, pageable, Optional.ofNullable(totalCount).orElse(0L));


            List<Long> countCheckList = queryFactory
                    .select(summary.id)
                    .from(summary)
                    .limit(maxCountLimit + 1)
                    .fetch();

            long totalCount = pageable.getOffset() + countCheckList.size();

            return new PageImpl<>(content, pageable, totalCount);
        }

    }

    private OrderSpecifier<?> getSortFromZone(MogakZoneSearchQuery query) {
        if (query.getSort().equals(MogakZoneSearchQuery.Sort.participant)) {
            return QMogakZoneEntity.mogakZoneEntity.participantNum.desc();
        } else {
            return QMogakZoneEntity.mogakZoneEntity.createdAt.desc();
        }
    }

    private OrderSpecifier<?> getSortFromSummary(MogakZoneSearchQuery query) {
        if (query.getSort().equals(MogakZoneSearchQuery.Sort.participant)) {
            return QZoneSummary.zoneSummary.participantNum.desc();
        } else {
            return QZoneSummary.zoneSummary.modifiedAt.desc();
        }
    }

}
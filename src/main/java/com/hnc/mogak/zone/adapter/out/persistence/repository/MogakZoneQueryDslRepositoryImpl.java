package com.hnc.mogak.zone.adapter.out.persistence.repository;

import com.hnc.mogak.member.adapter.out.persistence.QMemberEntity;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import com.hnc.mogak.zone.adapter.out.persistence.entity.QMogakZoneEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.QTagEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.QZoneMemberEntity;
import com.hnc.mogak.zone.adapter.out.persistence.entity.QZoneTagEntity;
import com.hnc.mogak.zone.application.port.in.query.MogakZoneSearchQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class MogakZoneQueryDslRepositoryImpl implements MogakZoneQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MogakZoneSearchResponse> findMogakZone(MogakZoneSearchQuery query, Pageable pageable) {
        QMogakZoneEntity mogakZone = QMogakZoneEntity.mogakZoneEntity;
        QZoneTagEntity zoneTag = QZoneTagEntity.zoneTagEntity;
        QTagEntity tag = QTagEntity.tagEntity;
        QZoneMemberEntity zoneMember = QZoneMemberEntity.zoneMemberEntity;
        QMemberEntity member = QMemberEntity.memberEntity;

        BooleanBuilder builder = new BooleanBuilder();

        if (query.getSearch() != null && !query.getSearch().isBlank()) {
            builder.and(mogakZone.name.containsIgnoreCase(query.getSearch()));
        }

        if (query.getTag() != null && !query.getTag().isBlank()) {
            builder.and(zoneTag.tag.name.eq(query.getTag()));
        }

        List<Tuple> zoneIdNameAndCount = queryFactory
                .select(mogakZone.id, mogakZone.name)
                .from(mogakZone)
                .leftJoin(zoneMember).on(zoneMember.mogakZoneEntity.eq(mogakZone))
                .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
                .leftJoin(tag).on(zoneTag.tag.eq(tag))
                .where(builder)
                .groupBy(mogakZone.id)
                .orderBy(getSort(query))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> zoneIds = zoneIdNameAndCount.stream()
                .map(t -> t.get(mogakZone.id))
                .toList();

        List<Tuple> tagTuples = queryFactory
                .select(zoneTag.zone.id, tag.name)
                .from(zoneTag)
                .join(tag).on(zoneTag.tag.eq(tag))
                .where(zoneTag.zone.id.in(zoneIds))
                .fetch();

        Map<Long, List<String>> zoneIdToTags = new HashMap<>();
        for (Tuple tuple : tagTuples) {
            Long zoneId = tuple.get(zoneTag.zone.id);
            String tagName = tuple.get(tag.name);
            zoneIdToTags.computeIfAbsent(zoneId, k -> new ArrayList<>()).add(tagName);
        }

        List<Tuple> memberTuples = queryFactory
                .select(zoneMember.mogakZoneEntity.id, member.imagePath)
                .from(zoneMember)
                .join(member).on(zoneMember.memberEntity.eq(member))
                .where(zoneMember.mogakZoneEntity.id.in(zoneIds))
                .fetch();

        Map<Long, List<String>> zoneIdToImages = new HashMap<>();
        for (Tuple tuple : memberTuples) {
            Long zoneId = tuple.get(zoneMember.mogakZoneEntity.id);
            String imagePath = tuple.get(member.imagePath);
            zoneIdToImages.computeIfAbsent(zoneId, k -> new ArrayList<>());
            List<String> images = zoneIdToImages.get(zoneId);

            if (images.size() <= 3) {
                if (imagePath == null) imagePath = "Default";
                images.add(imagePath);
            }
        }

        List<MogakZoneSearchResponse> content = zoneIdNameAndCount.stream()
                .map(tuple -> {
                    Long zoneId = tuple.get(mogakZone.id);
                    String name = tuple.get(mogakZone.name);
                    List<String> tags = zoneIdToTags.getOrDefault(zoneId, List.of());
                    List<String> images = zoneIdToImages.getOrDefault(zoneId, List.of());
                    return MogakZoneSearchResponse.builder()
                            .mogakZoneId(zoneId)
                            .name(name)
                            .tagNames(tags)
                            .memberImageUrls(images)
                            .build();
                }).toList();

        Long total = queryFactory
                .select(mogakZone.countDistinct())
                .from(mogakZone)
                .leftJoin(zoneTag).on(zoneTag.zone.eq(mogakZone))
                .leftJoin(tag).on(zoneTag.tag.eq(tag))
                .where(builder)
                .fetchOne();

        long safeTotal = Optional.ofNullable(total).orElse(0L);
        return new PageImpl<>(content, pageable, safeTotal);
    }

    private OrderSpecifier<?> getSort(MogakZoneSearchQuery query) {
        if (query.getSort().equals(MogakZoneSearchQuery.Sort.participant)) {
            return QZoneMemberEntity.zoneMemberEntity.count().desc();
        } else {
            return QMogakZoneEntity.mogakZoneEntity.createdAt.desc();
        }
    }
}

package com.hnc.mogak.challenge.adapter.out.persistence.entity;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "challenge_badge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ChallengeBadgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private BadgeEntity badgeEntity;

    @OneToOne(fetch = FetchType.LAZY)
    private ChallengeEntity challengeEntity;

}
package com.hnc.mogak.challenge.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "challenge_article")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeArticleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_article_id")
    private Long id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private ChallengeEntity challengeEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @OneToMany(mappedBy = "challengeArticleEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Builder.Default
    private List<ChallengeImageEntity> challengeImageEntityList = new ArrayList<>();

}

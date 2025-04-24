package com.hnc.mogak.challenge.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "challenge_owner")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeOwnerEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_owner_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private ChallengeEntity challengeEntity;

}

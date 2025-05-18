package com.hnc.mogak.zone.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import com.hnc.mogak.member.adapter.out.persistence.MemberEntity;
import com.hnc.mogak.zone.domain.zonemember.vo.ZoneMemberStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "zone_member")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ZoneMemberEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private ZoneMemberStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private MogakZoneEntity mogakZoneEntity;

}

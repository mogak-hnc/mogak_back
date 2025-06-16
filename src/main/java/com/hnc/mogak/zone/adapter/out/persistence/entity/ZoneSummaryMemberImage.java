package com.hnc.mogak.zone.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "zone_summary_member_image", indexes = @Index(name = "idx_member_id", columnList = "memberId"))
public class ZoneSummaryMemberImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mogak_zone_id")
    private Long mogakZoneId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "image_url")
    private String imageUrl;

}
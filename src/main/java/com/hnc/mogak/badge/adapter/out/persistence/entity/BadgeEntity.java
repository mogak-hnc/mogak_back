package com.hnc.mogak.badge.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "badge")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadgeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false)
    private String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BadgeType badgeType;

}

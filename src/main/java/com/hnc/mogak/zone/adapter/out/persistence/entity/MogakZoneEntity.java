package com.hnc.mogak.zone.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "mogak_zone")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MogakZoneEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "max_capacity")
    private int maxCapacity;

    @Column(name = "image_url", length = 1024)
    private String imageUrl;

    @Column(name = "password")
    private String password;

    @Column(name = "chat_enabled")
    private boolean chatEnabled;


}
package com.hnc.mogak.challenge.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@Table(name = "challenge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "total_participants")
    private int totalParticipants;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "official")
    private boolean official;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ChallengeStatus status;

    public void updateChallengeStatus(ChallengeStatus newStatus) {
        this.status = newStatus;
    }

}
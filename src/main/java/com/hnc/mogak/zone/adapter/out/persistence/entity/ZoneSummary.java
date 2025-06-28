package com.hnc.mogak.zone.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "zone_summary")
@Getter
public class ZoneSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long mogakZoneId;
    private String name;
    private boolean passwordRequired;

    private Long participantNum;

    public void increaseJoinCount() {
        this.participantNum++;
    }

    public void decreaseJoinCount() {
        if (this.participantNum > 0) {
            this.participantNum--;
        }
    }

}
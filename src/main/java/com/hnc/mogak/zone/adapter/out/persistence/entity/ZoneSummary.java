package com.hnc.mogak.zone.adapter.out.persistence.entity;

import com.hnc.mogak.global.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "zone_summary",
        indexes = {
                @Index(name = "idx_mogak_zone_id", columnList = "mogakZoneId"),
                @Index(name = "idx_join_count_desc", columnList = "joinCount DESC")
        }
)
@Getter
public class ZoneSummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long mogakZoneId;

    @Column(length = 500)
    private String tagNames;

    private String name;

    private Long joinCount;

    @Column(length = 1000)
    private String memberImageUrls;

    public void increaseJoinCount(String memberImageUrl) {
        this.joinCount++;

        if (joinCount <= 4) {
            if (this.memberImageUrls == null || this.memberImageUrls.isEmpty()) {
                this.memberImageUrls = memberImageUrl;
            } else {
                this.memberImageUrls = this.memberImageUrls + " " + memberImageUrl;
            }
        }
    }

    public void decreaseJoinCount(String memberImageUrl) {
        if (this.joinCount > 0) {
            this.joinCount--;
        }

        if (this.joinCount <= 4 && this.memberImageUrls != null && !this.memberImageUrls.isEmpty()) {
            String[] images = this.memberImageUrls.split(" ");

            List<String> imageList = new ArrayList<>(List.of(images));
            imageList.remove(memberImageUrl);

            this.memberImageUrls = String.join(" ", imageList);
        }
    }

}
package com.hnc.mogak.badge.adapter.in.web.dto;

import com.hnc.mogak.badge.adapter.out.persistence.entity.BadgeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateBadgeRequest {

    @NotBlank(message = "뱃지 이름은 필수입니다")
    private String name;

    @NotBlank(message = "뱃지 설명은 필수입니다")
    @Size(max = 200, message = "뱃지 설명은 200자를 초과할 수 없습니다")
    private String description;

    @NotNull(message = "뱃지 타입은 필수입니다")
    private BadgeType badgeType;

    private int conditionValue;

}
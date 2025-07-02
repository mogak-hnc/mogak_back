package com.hnc.mogak.main;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.global.PageResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MainPageResponse {

    private PageResponse<MogakZoneSearchResponse> mogakZoneMainResponses;
    private PageResponse<ChallengeSearchResponse> mogakChallengeResponses;

}

package com.hnc.mogak.main;

import com.hnc.mogak.challenge.adapter.in.web.dto.ChallengeSearchResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneSearchResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
public class MainPageResponse {

    private Page<MogakZoneSearchResponse> mogakZoneMainResponses;
    private Page<ChallengeSearchResponse> mogakChallengeResponses;

}

package com.hnc.mogak.main;

import com.hnc.mogak.challenge.adapter.in.web.dto.MogakChallengeMainResponse;
import com.hnc.mogak.zone.adapter.in.web.dto.MogakZoneMainResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MainPage {

    private List<MogakZoneMainResponse> mogakZoneMainResponses;
    private List<MogakChallengeMainResponse> mogakChallengeResponses;
}

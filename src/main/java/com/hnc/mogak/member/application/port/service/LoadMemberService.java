package com.hnc.mogak.member.application.port.service;

import com.hnc.mogak.member.application.port.in.LoadMemberUseCase;
import com.hnc.mogak.member.application.port.out.LoadMemberPort;
import com.hnc.mogak.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class LoadMemberService implements LoadMemberUseCase {

    private final LoadMemberPort loadMemberPort;

    @Override
    public Member loadMemberByProviderId(String providerId) {
        return loadMemberPort.loadMemberByProviderId(providerId);
    }

}
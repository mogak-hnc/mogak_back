package com.hnc.mogak.worry.service;

import com.hnc.mogak.worry.dto.*;

import java.util.List;

public interface WorryService {

    CreateWorryResponse create(CreateWorryRequest request, Long memberId);

    WorryDetailResponse getWorry(Integer worryId);

    WorryEmpathyResponse toggleEmpathy(Integer worryId, String memberId);

    CommentResponse createComment(CreateWorryCommentRequest request, String memberId, Integer worryId);

    List<WorryPreview> getWorryList(String sort, int start, int end);

}
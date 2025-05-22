package com.hnc.mogak.worry.service;

import com.hnc.mogak.global.PageResponse;
import com.hnc.mogak.worry.dto.*;

public interface WorryService {

    CreateWorryResponse create(CreateWorryRequest request, Long memberId);

    WorryDetailResponse getWorryDetail(Integer worryId, String memberId);

    WorryEmpathyResponse toggleEmpathy(Integer worryId, String memberId);

    CommentResponse createComment(CreateWorryCommentRequest request, Long memberId, Integer worryId);

    PageResponse<WorryPreview> getWorryList(String sort, int page, int size);

}
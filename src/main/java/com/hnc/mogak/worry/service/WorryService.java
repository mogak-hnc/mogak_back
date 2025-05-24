package com.hnc.mogak.worry.service;

import com.hnc.mogak.global.PageResponse;
import com.hnc.mogak.worry.dto.*;

public interface WorryService {

    CreateWorryResponse create(CreateWorryRequest request, Long memberId);

    WorryArticleResponse getWorryArticle(Integer worryId, String memberId);

    WorryEmpathyResponse toggleEmpathy(Integer worryId, String memberId);

    WorryCommentResponse createComment(CreateWorryCommentRequest request, Long memberId, Integer worryId);

    PageResponse<WorryPreview> getWorryList(String sort, int page, int size);

    PageResponse<WorryCommentResponse> getWorryComments(Integer worryId, int page, int size);

    WorryArticleDeleteResponse deleteWorryArticle(Integer worryId, Long memberId, String role);

    WorryCommentDeleteResponse deleteWorryComment(Integer worryId, Long memberId, Integer commentId, String role);

}
package com.hnc.mogak.challenge.adapter.out.persistence.projection;

public interface GetChallengeArticleThumbNailProjection {

    Long getMemberId();
    Long getChallengeArticleId();
    String getThumbnailUrl();

}

package com.hnc.mogak.worry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiRetryQueue {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;
    private Integer worryId;

    private RetryStatus retryStatus;
    private int retryCount;


    public AiRetryQueue(String title, String body, Integer worryId) {
        this.title = title;
        this.body = body;
        this.worryId = worryId;
        this.retryStatus = RetryStatus.PENDING;
        this.retryCount = 0;
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }

    public void updateRetryStatus(RetryStatus retryStatus) {
        this.retryStatus = retryStatus;
    }

}
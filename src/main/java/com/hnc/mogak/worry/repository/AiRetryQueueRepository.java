package com.hnc.mogak.worry.repository;

import com.hnc.mogak.worry.entity.AiRetryQueue;
import com.hnc.mogak.worry.entity.RetryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiRetryQueueRepository extends JpaRepository<AiRetryQueue, Long> {


    List<AiRetryQueue> findAllByRetryStatus(RetryStatus retryStatus);

}

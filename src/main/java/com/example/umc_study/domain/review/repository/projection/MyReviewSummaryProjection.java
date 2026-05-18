package com.example.umc_study.domain.review.repository.projection;

import java.time.LocalDateTime;

public interface MyReviewSummaryProjection {
    Long getReviewId();

    String getStoreName();

    String getTitle();

    String getBody();

    Float getScore();

    LocalDateTime getCreatedAt();
}

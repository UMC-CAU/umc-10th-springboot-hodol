package com.example.umc_study.domain.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewReqDTO {

    @Getter
    @NoArgsConstructor
    public static class CreateReviewDTO {
        private Long memberId;
        private String title;
        private Float score;    // 소수점 별점
        private String body;    // 리뷰 내용
    }
}

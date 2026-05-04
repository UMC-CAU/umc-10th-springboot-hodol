package com.example.umc_study.domain.review.dto;

import lombok.Getter;

public class ReviewReqDTO {

    @Getter
    public static class CreateReviewDTO {
        private Double score;    // 소수점 별점
        private String content;  // 리뷰 내용
        private String image;    // 이미지 URL
    }
}

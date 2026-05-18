package com.example.umc_study.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateResultDTO {
        private Long reviewId;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyReviewListDTO {
        private List<MyReviewDetailDTO> reviewList;
        private CursorPaginationDTO pagination;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyReviewDetailDTO {
        private Long reviewId;
        private String storeName;
        private String title;
        private String body;
        private Float score;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursorPaginationDTO {
        private Long nextCursorId;
        private Float nextCursorScore;
        private Integer size;
        private Boolean hasNext;
    }
}

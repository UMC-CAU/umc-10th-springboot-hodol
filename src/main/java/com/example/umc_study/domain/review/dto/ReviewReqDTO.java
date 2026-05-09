package com.example.umc_study.domain.review.dto;

import com.example.umc_study.domain.review.enums.ReviewSortType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewReqDTO {

    @Getter
    @NoArgsConstructor
    public static class CreateReviewDTO {
        @NotNull
        private Long memberId;

        private String title;

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("5.0")
        private Float score;

        @NotBlank
        private String body;
    }

    @Getter
    @NoArgsConstructor
    public static class GetMyReviewListDTO {
        @NotNull
        @Positive
        private Long memberId;

        @Positive
        private Long cursorId;

        @DecimalMin("0.0")
        @DecimalMax("5.0")
        private Float cursorScore;

        @NotNull
        @Positive
        private Integer size = 10;

        @NotNull
        private ReviewSortType sortType = ReviewSortType.ID;
    }
}

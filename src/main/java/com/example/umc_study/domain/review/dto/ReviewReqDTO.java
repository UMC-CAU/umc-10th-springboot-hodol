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
        @NotNull(message = "memberId is required.")
        @Positive(message = "memberId must be a positive number.")
        private Long memberId;

        private String title;

        @NotNull(message = "score is required.")
        @DecimalMin(value = "0.0", message = "score must be between 0 and 5.")
        @DecimalMax(value = "5.0", message = "score must be between 0 and 5.")
        private Float score;

        @NotBlank(message = "body is required.")
        private String body;
    }

    @Getter
    @NoArgsConstructor
    public static class GetMyReviewListDTO {
        @NotNull(message = "memberId is required.")
        @Positive(message = "memberId must be a positive number.")
        private Long memberId;

        @Positive(message = "cursorId must be a positive number.")
        private Long cursorId;

        @DecimalMin(value = "0.0", message = "cursorScore must be between 0 and 5.")
        @DecimalMax(value = "5.0", message = "cursorScore must be between 0 and 5.")
        private Float cursorScore;

        @NotNull(message = "size is required.")
        @Positive(message = "size must be a positive number.")
        private Integer size = 10;

        @NotNull(message = "sortType is required.")
        private ReviewSortType sortType = ReviewSortType.ID;
    }
}

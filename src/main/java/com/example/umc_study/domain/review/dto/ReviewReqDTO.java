package com.example.umc_study.domain.review.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
}

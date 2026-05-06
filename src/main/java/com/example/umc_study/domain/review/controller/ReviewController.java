package com.example.umc_study.domain.review.controller;

import com.example.umc_study.domain.review.dto.ReviewReqDTO;
import com.example.umc_study.domain.review.dto.ReviewResDTO;
import com.example.umc_study.domain.review.exception.code.ReviewSuccessCode;
import com.example.umc_study.domain.review.service.ReviewService;
import com.example.umc_study.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/stores/{storeId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResDTO.CreateResultDTO>> createReview(
            @PathVariable(name = "storeId") Long storeId,
            @RequestBody ReviewReqDTO.CreateReviewDTO request
    ) {
        ReviewResDTO.CreateResultDTO result = reviewService.createReview(storeId, request);
        return ResponseEntity.status(ReviewSuccessCode.REVIEW_CREATED.getStatus())
                .body(ApiResponse.onSuccess(ReviewSuccessCode.REVIEW_CREATED, result));
    }
}

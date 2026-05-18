package com.example.umc_study.domain.review.controller;

import com.example.umc_study.domain.review.dto.ReviewReqDTO;
import com.example.umc_study.domain.review.dto.ReviewResDTO;
import com.example.umc_study.domain.review.service.ReviewService;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/reviews/me")
    public ApiResponse<ReviewResDTO.MyReviewListDTO> getMyReviews(
            @Valid @RequestBody ReviewReqDTO.GetMyReviewListDTO request
    ) {
        ReviewResDTO.MyReviewListDTO result = reviewService.getMyReviews(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PostMapping("/stores/{storeId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReviewResDTO.CreateResultDTO> createReview(
            @PathVariable(name = "storeId") Long storeId,
            @Valid @RequestBody ReviewReqDTO.CreateReviewDTO request
    ) {
        ReviewResDTO.CreateResultDTO result = reviewService.createReview(storeId, request);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}

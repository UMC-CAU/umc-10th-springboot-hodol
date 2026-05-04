package com.example.umc_study.domain.review.controller;


import com.example.umc_study.domain.review.dto.ReviewReqDTO;
import com.example.umc_study.domain.review.dto.ReviewResDTO;
import com.example.umc_study.domain.review.exception.code.ReviewSuccessCode;
import com.example.umc_study.domain.review.service.ReviewService;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.BaseSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{storeId}/reviews")
    public ApiResponse<ReviewResDTO.CreateResultDTO> createReview(
            @PathVariable(name = "storeId") Long storeId,
            @RequestHeader(name = "Authorization") String token,
            @RequestBody ReviewReqDTO.CreateReviewDTO request
    ) {
        ReviewResDTO.CreateResultDTO result = reviewService.createReview(storeId, request);

        return ApiResponse.onSuccess(ReviewSuccessCode.OK, result);
    }
}

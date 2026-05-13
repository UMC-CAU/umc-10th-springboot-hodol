package com.example.umc_study.domain.review.exception.code;

import com.example.umc_study.global.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccessCode implements BaseSuccessCode {
    REVIEW_CREATED(HttpStatus.CREATED, "REVIEW201", "Review created successfully.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

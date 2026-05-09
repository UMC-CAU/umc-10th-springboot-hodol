package com.example.umc_study.domain.review.exception.code;

import com.example.umc_study.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {
    REVIEW_MEMBER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "REVIEW400_1", "memberId is required."),
    REVIEW_SCORE_INVALID(HttpStatus.BAD_REQUEST, "REVIEW400_2", "score must be between 0 and 5."),
    REVIEW_BODY_REQUIRED(HttpStatus.BAD_REQUEST, "REVIEW400_3", "body is required."),
    REVIEW_CURSOR_INVALID(HttpStatus.BAD_REQUEST, "REVIEW400_4", "cursor values are invalid for the selected sort type."),
    REVIEW_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_1", "member not found."),
    REVIEW_STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW404_2", "store not found.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

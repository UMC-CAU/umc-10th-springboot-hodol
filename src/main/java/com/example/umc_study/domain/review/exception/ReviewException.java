package com.example.umc_study.domain.review.exception;

import com.example.umc_study.global.code.BaseErrorCode;
import com.example.umc_study.global.exception.ProjectException;

public class ReviewException extends ProjectException {
    public ReviewException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

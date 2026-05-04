package com.example.umc_study.global.exception;

import com.example.umc_study.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor

public class ProjectException extends RuntimeException {
    private final BaseErrorCode errorCode;
}

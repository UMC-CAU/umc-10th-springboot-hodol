package com.example.umc_study.global.code;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

    HttpStatus getStatus();
    String getCode();
    String getMessage();
}

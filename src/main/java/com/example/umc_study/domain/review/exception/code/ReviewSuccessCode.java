package com.example.umc_study.domain.review.exception.code;

import com.example.umc_study.global.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewSuccessCode implements BaseSuccessCode {

    OK(HttpStatus.OK, "MISSION200", "요청에 성공하였습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;


}

package com.example.umc_study.domain.member.exception.code;

import com.example.umc_study.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {

    MEMBER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_1", "member id is required."),
    MEMBER_ID_INVALID(HttpStatus.BAD_REQUEST, "MEMBER400_2", "member id must be a positive number."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "member not found."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

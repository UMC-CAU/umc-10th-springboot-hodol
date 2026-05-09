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
    MEMBER_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_3", "name is required."),
    MEMBER_PASSWORD_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_4", "password is required."),
    MEMBER_AGE_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_5", "age is required."),
    MEMBER_AGE_INVALID(HttpStatus.BAD_REQUEST, "MEMBER400_6", "age must be a positive number."),
    MEMBER_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_7", "email is required."),
    MEMBER_EMAIL_INVALID(HttpStatus.BAD_REQUEST, "MEMBER400_8", "email must be a valid email address."),
    MEMBER_GENDER_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_9", "gender is required."),
    MEMBER_NICKNAME_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_10", "nickname is required."),
    MEMBER_PHONE_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_11", "phone number is required."),
    MEMBER_BIRTH_DATE_REQUIRED(HttpStatus.BAD_REQUEST, "MEMBER400_12", "birth date is required."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404_1", "member not found."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

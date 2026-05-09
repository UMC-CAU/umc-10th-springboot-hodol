package com.example.umc_study.domain.mission.exception.code;

import com.example.umc_study.global.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements BaseErrorCode {

    MISSION_MEMBER_ID_REQUIRED(HttpStatus.BAD_REQUEST, "MISSION400_1", "memberId is required."),
    MISSION_MEMBER_ID_INVALID(HttpStatus.BAD_REQUEST, "MISSION400_2", "memberId must be a positive number."),
    MISSION_OFFSET_REQUIRED(HttpStatus.BAD_REQUEST, "MISSION400_3", "offset is required."),
    MISSION_OFFSET_INVALID(HttpStatus.BAD_REQUEST, "MISSION400_4", "offset must be zero or a positive number."),
    MISSION_LIMIT_REQUIRED(HttpStatus.BAD_REQUEST, "MISSION400_5", "limit is required."),
    MISSION_LIMIT_INVALID(HttpStatus.BAD_REQUEST, "MISSION400_6", "limit must be a positive number.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}

package com.example.umc_study.domain.mission.exception.code;

import com.example.umc_study.global.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MissionSuccessCode implements BaseSuccessCode {

    OK(HttpStatus.OK, "MISSION200", "요청에 성공하였습니다."),
    MISSION_CHALLENGED(HttpStatus.CREATED, "MISSION201", "미션 도전에 성공하였습니다."),
    MISSION_COMPLETED(HttpStatus.OK, "MISSION202", "미션을 완료하였습니다.");
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}

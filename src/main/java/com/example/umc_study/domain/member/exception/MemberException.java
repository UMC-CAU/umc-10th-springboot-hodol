package com.example.umc_study.domain.member.exception;

import com.example.umc_study.global.code.BaseErrorCode;
import com.example.umc_study.global.exception.ProjectException;

public class MemberException extends ProjectException {
    public MemberException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}

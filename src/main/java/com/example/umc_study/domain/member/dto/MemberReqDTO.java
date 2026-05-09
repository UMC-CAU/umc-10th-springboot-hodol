package com.example.umc_study.domain.member.dto;

import com.example.umc_study.domain.member.enums.Gender;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class MemberReqDTO {


    public record JoinDTO (
        String name,
        String password,
        Integer age,
        String email,
        Gender gender,    // Enum 사용 추천
        String nickName,
        String phoneNumber,
        LocalDate birthDate // 2003-08-04 형식 자동 파싱
    ){}

    public record GetInfo(
            @NotNull(message = "member id is required.")
            @Positive(message = "member id must be a positive number.")
            Long id
    ){}

    public record RequestBody(
            String stringTest,
            Long longTest
    ){}
}

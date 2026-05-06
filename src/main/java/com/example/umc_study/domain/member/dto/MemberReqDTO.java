package com.example.umc_study.domain.member.dto;

import com.example.umc_study.domain.member.enums.Gender;
import lombok.Getter;

import java.time.LocalDate;

public class MemberReqDTO {


    public record JoinDTO (
        String name;
        String password;
        Integer age;
        String email;
        Gender gender;    // Enum 사용 추천
        String nickName;
        String phoneNumber;
        LocalDate birthDate; // 2003-08-04 형식 자동 파싱
    ){}

    public record GetInfo(
            Long id
    ){}
}

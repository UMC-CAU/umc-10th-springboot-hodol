package com.example.umc_study.domain.member.dto;

import com.example.umc_study.domain.member.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class MemberReqDTO {

    public record JoinDTO(
            @NotBlank(message = "name is required.")
            String name,
            @NotBlank(message = "password is required.")
            String password,
            @NotNull(message = "age is required.")
            @Positive(message = "age must be a positive number.")
            Integer age,
            @NotBlank(message = "email is required.")
            @Email(message = "email must be a valid email address.")
            String email,
            @NotNull(message = "gender is required.")
            Gender gender,
            @NotBlank(message = "nickname is required.")
            String nickName,
            @NotBlank(message = "phone number is required.")
            String phoneNumber,
            @NotNull(message = "birth date is required.")
            LocalDate birthDate
    ) {
    }

    public record GetInfo(
            @NotNull(message = "member id is required.")
            @Positive(message = "member id must be a positive number.")
            Long id
    ) {
    }

    public record RequestBody(
            String stringTest,
            Long longTest
    ) {
    }
}

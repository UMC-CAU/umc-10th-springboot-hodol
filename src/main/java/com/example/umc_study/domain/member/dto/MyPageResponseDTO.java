package com.example.umc_study.domain.member.dto;

public record MyPageResponseDTO(
        ProfileInfo profile,
        ActivitySummary activitySummary
) {

    public record ProfileInfo(
            String nickname,
            String email,
            PhoneInfo phoneInfo
    ) {
    }

    public record PhoneInfo(
            String phoneNumber,
            Boolean verified
    ) {
    }

    public record ActivitySummary(
            Integer currentPointBalance,
            Long reviewCount
    ) {
    }
}

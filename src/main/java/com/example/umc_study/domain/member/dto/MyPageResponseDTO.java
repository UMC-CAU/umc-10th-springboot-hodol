package com.example.umc_study.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDTO {

    private ProfileInfo profile;
    private ActivitySummary activitySummary;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileInfo {
        private String nickname;
        private String email;
        private PhoneInfo phoneInfo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PhoneInfo {
        private String phoneNumber;
        private Boolean verified;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivitySummary {
        private Integer currentPointBalance;
        private Long reviewCount;
    }
}

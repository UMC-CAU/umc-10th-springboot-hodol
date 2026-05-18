package com.example.umc_study.domain.mission.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class MissionReqDTO {

    public record ChallengeDTO(
            Long memberId
    ) {
    }

    public record GetProgressMissionListDTO(
            @NotNull(message = "memberId is required.")
            @Positive(message = "memberId must be a positive number.")
            Long memberId,
            @NotNull(message = "offset is required.")
            @PositiveOrZero(message = "offset must be zero or a positive number.")
            Integer offset,
            @NotNull(message = "limit is required.")
            @Positive(message = "limit must be a positive number.")
            Integer limit
    ) {
    }

    public record CreateMissionDTO(
            String title,
            String missionSpec,
            Integer reward,
            String deadline
    ) {
    }
}

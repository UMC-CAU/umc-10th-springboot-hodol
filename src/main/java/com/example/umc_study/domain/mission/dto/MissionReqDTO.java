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
            @NotNull Long memberId,
            @NotNull @PositiveOrZero Integer offset,
            @NotNull @Positive Integer limit
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

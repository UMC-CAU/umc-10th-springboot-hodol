package com.example.umc_study.domain.mission.repository.projection;

import java.time.LocalDate;

public interface HomeMissionSummaryProjection {
    Long getMissionId();

    String getStoreName();

    String getMissionSpec();

    Integer getReward();

    LocalDate getDeadline();

    String getCategory();
}

package com.example.umc_study.domain.mission.repository.projection;

import com.example.umc_study.domain.mission.enums.MissionStatus;

public interface MemberMissionSummaryProjection {
    Long getMemberMissionId();

    Long getMissionId();

    String getStoreName();

    String getMissionSpec();

    Integer getReward();

    MissionStatus getStatus();
}

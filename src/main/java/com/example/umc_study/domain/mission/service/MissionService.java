package com.example.umc_study.domain.mission.service;

import com.example.umc_study.domain.mission.dto.MissionResDTO;
import com.example.umc_study.domain.mission.enums.MissionStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class MissionService {

    public MissionResDTO.MissionListDTO getMyMissions(MissionStatus status) {
        // TODO: 실제 구현 필요. 컴파일 에러 방지용.
        return MissionResDTO.MissionListDTO.builder()
                .missionList(Collections.emptyList())
                .listSize(0)
                .build();
    }

    public MissionResDTO.MissionCompleteResultDTO completeMission(Long missionId) {
        // TODO: 실제 구현 필요. 컴파일 에러 방지용.
        return MissionResDTO.MissionCompleteResultDTO.builder()
                .missionId(missionId)
                .status("COMPLETED")
                .completedAt(LocalDateTime.now())
                .build();
    }
}

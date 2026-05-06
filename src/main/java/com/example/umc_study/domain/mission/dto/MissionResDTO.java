package com.example.umc_study.domain.mission.dto;

import com.example.umc_study.domain.mission.enums.MissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class MissionResDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionListDTO {
        private List<MissionDetailDTO> missionList;
        private Integer listSize;
        private Integer pageNumber;
        private Integer pageSize;
        private Integer totalPages;
        private Long totalElements;
        private Boolean first;
        private Boolean last;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionDetailDTO {
        private Long memberMissionId;
        private Long missionId;
        private String storeName;
        private String missionSpec;
        private String rewardPoint;
        private MissionStatus status;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionCompleteResultDTO {
        private Long missionId;
        private String status;
        private LocalDateTime completedAt;
    }
}

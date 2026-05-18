package com.example.umc_study.domain.mission.dto;

import com.example.umc_study.domain.mission.enums.MissionStatus;
import com.example.umc_study.global.common.Pagination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MissionResDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionListDTO {
        private List<MissionDetailDTO> missionList;
        private Pagination pagination;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgressMissionListDTO {
        private List<MissionDetailDTO> missionList;
        private OffsetPaginationDTO pagination;
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
    public static class HomeMissionListDTO {
        private MissionProgressDTO progress;
        private List<HomeMissionDetailDTO> missionList;
        private Pagination pagination;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OffsetPaginationDTO {
        private Integer offset;
        private Integer limit;
        private Integer listSize;
        private Long totalElements;
        private Boolean hasNext;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionProgressDTO {
        private Integer currentMissionCount;
        private Integer targetMissionCount;
        private String targetRewardPoint;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HomeMissionDetailDTO {
        private Long missionId;
        private String storeName;
        private String missionSpec;
        private String rewardPoint;
        private LocalDate deadline;
        private String dDay;
        private String category;
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

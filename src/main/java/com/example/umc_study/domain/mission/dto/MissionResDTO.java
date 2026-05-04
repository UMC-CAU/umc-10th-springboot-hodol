package com.example.umc_study.domain.mission.dto;

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
        private Integer listSize; // 목록 개수
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionDetailDTO {
        private Long missionId;
        private String storeName; // 가게 이름
        private Integer reward;    // 리워드 포인트
        private String deadline;  // 마감 기한
        private String missionSpec; // 미션 내용
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissionCompleteResultDTO {
        private Long missionId;       // 완료된 미션 식별자
        private String status;        // 변경된 상태 (예: COMPLETED)
        private LocalDateTime completedAt; // 완료 처리된 시각
    }
}

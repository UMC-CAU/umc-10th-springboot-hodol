package com.example.umc_study.domain.mission.converter;

import com.example.umc_study.domain.mission.dto.MissionResDTO;
import com.example.umc_study.domain.mission.repository.projection.HomeMissionSummaryProjection;
import com.example.umc_study.domain.mission.repository.projection.MemberMissionSummaryProjection;
import com.example.umc_study.global.common.Pagination;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MissionConverter {

    public static MissionResDTO.MissionListDTO toMissionListDTO(Page<MemberMissionSummaryProjection> memberMissionPage) {
        List<MissionResDTO.MissionDetailDTO> missionList = memberMissionPage.getContent().stream()
                .map(MissionConverter::toMissionDetailDTO)
                .toList();

        return MissionResDTO.MissionListDTO.builder()
                .missionList(missionList)
                .pagination(Pagination.from(memberMissionPage))
                .build();
    }

    public static MissionResDTO.ProgressMissionListDTO toProgressMissionListDTO(
            Page<MemberMissionSummaryProjection> memberMissionPage
    ) {
        List<MissionResDTO.MissionDetailDTO> missionList = memberMissionPage.getContent().stream()
                .map(MissionConverter::toMissionDetailDTO)
                .toList();

        return MissionResDTO.ProgressMissionListDTO.builder()
                .missionList(missionList)
                .pagination(
                        MissionResDTO.OffsetPaginationDTO.builder()
                                .offset(Math.toIntExact(memberMissionPage.getPageable().getOffset()))
                                .limit(memberMissionPage.getSize())
                                .listSize(memberMissionPage.getNumberOfElements())
                                .totalElements(memberMissionPage.getTotalElements())
                                .hasNext(memberMissionPage.hasNext())
                                .build()
                )
                .build();
    }

    public static MissionResDTO.MissionDetailDTO toMissionDetailDTO(MemberMissionSummaryProjection memberMission) {
        return MissionResDTO.MissionDetailDTO.builder()
                .memberMissionId(memberMission.getMemberMissionId())
                .missionId(memberMission.getMissionId())
                .storeName(memberMission.getStoreName())
                .missionSpec(memberMission.getMissionSpec())
                .rewardPoint(memberMission.getReward() + "P")
                .status(memberMission.getStatus())
                .build();
    }

    public static MissionResDTO.HomeMissionListDTO toHomeMissionListDTO(
            Page<HomeMissionSummaryProjection> missionPage,
            long currentMissionCount,
            int targetMissionCount,
            int targetRewardPoint
    ) {
        List<MissionResDTO.HomeMissionDetailDTO> missionList = missionPage.getContent().stream()
                .map(MissionConverter::toHomeMissionDetailDTO)
                .toList();

        return MissionResDTO.HomeMissionListDTO.builder()
                .progress(
                        MissionResDTO.MissionProgressDTO.builder()
                                .currentMissionCount((int) currentMissionCount)
                                .targetMissionCount(targetMissionCount)
                                .targetRewardPoint(targetRewardPoint + "P")
                                .build()
                )
                .missionList(missionList)
                .pagination(Pagination.from(missionPage))
                .build();
    }

    public static MissionResDTO.HomeMissionDetailDTO toHomeMissionDetailDTO(HomeMissionSummaryProjection mission) {
        return MissionResDTO.HomeMissionDetailDTO.builder()
                .missionId(mission.getMissionId())
                .storeName(mission.getStoreName())
                .missionSpec(mission.getMissionSpec())
                .rewardPoint(mission.getReward() + "P")
                .deadline(mission.getDeadline())
                .dDay(toDDay(mission.getDeadline()))
                .category(mission.getCategory())
                .build();
    }

    private static String toDDay(LocalDate deadline) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), deadline);
        if (days == 0) {
            return "D-DAY";
        }
        if (days > 0) {
            return "D-" + days;
        }
        return "D+" + Math.abs(days);
    }

}

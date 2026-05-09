package com.example.umc_study.domain.mission.converter;

import com.example.umc_study.domain.mission.dto.MissionResDTO;
import com.example.umc_study.domain.mission.entity.Mission;
import com.example.umc_study.domain.mission.entity.mapping.MemberMission;
import com.example.umc_study.global.common.Pagination;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MissionConverter {

    public static MissionResDTO.MissionListDTO toMissionListDTO(Page<MemberMission> memberMissionPage) {
        List<MissionResDTO.MissionDetailDTO> missionList = memberMissionPage.getContent().stream()
                .map(MissionConverter::toMissionDetailDTO)
                .toList();

        return MissionResDTO.MissionListDTO.builder()
                .missionList(missionList)
                .pagination(toPagination(memberMissionPage))
                .build();
    }

    public static MissionResDTO.ProgressMissionListDTO toProgressMissionListDTO(
            Page<MemberMission> memberMissionPage,
            int offset,
            int limit
    ) {
        List<MissionResDTO.MissionDetailDTO> missionList = memberMissionPage.getContent().stream()
                .map(MissionConverter::toMissionDetailDTO)
                .toList();

        return MissionResDTO.ProgressMissionListDTO.builder()
                .missionList(missionList)
                .pagination(
                        MissionResDTO.OffsetPaginationDTO.builder()
                                .offset(offset)
                                .limit(limit)
                                .listSize(missionList.size())
                                .totalElements(memberMissionPage.getTotalElements())
                                .hasNext(memberMissionPage.hasNext())
                                .build()
                )
                .build();
    }

    public static MissionResDTO.MissionDetailDTO toMissionDetailDTO(MemberMission memberMission) {
        return MissionResDTO.MissionDetailDTO.builder()
                .memberMissionId(memberMission.getId())
                .missionId(memberMission.getMission().getId())
                .storeName(memberMission.getMission().getStore().getName())
                .missionSpec(memberMission.getMission().getMissionSpec())
                .rewardPoint(memberMission.getMission().getReward() + "P")
                .status(memberMission.getStatus())
                .build();
    }

    public static MissionResDTO.HomeMissionListDTO toHomeMissionListDTO(
            Page<Mission> missionPage,
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
                .pagination(toPagination(missionPage))
                .build();
    }

    public static MissionResDTO.HomeMissionDetailDTO toHomeMissionDetailDTO(Mission mission) {
        return MissionResDTO.HomeMissionDetailDTO.builder()
                .missionId(mission.getId())
                .storeName(mission.getStore().getName())
                .missionSpec(mission.getMissionSpec())
                .rewardPoint(mission.getReward() + "P")
                .deadline(mission.getDeadline())
                .dDay(toDDay(mission.getDeadline()))
                .category(mission.getStore().getCategory())
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

    private static Pagination toPagination(Page<?> page) {
        return Pagination.builder()
                .listSize(page.getNumberOfElements())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}

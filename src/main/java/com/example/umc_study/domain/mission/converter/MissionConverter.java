package com.example.umc_study.domain.mission.converter;

import com.example.umc_study.domain.mission.dto.MissionResDTO;
import com.example.umc_study.domain.mission.entity.mapping.MemberMission;
import org.springframework.data.domain.Page;

import java.util.List;

public class MissionConverter {

    public static MissionResDTO.MissionListDTO toMissionListDTO(Page<MemberMission> memberMissionPage) {
        List<MissionResDTO.MissionDetailDTO> missionList = memberMissionPage.getContent().stream()
                .map(MissionConverter::toMissionDetailDTO)
                .toList();

        return MissionResDTO.MissionListDTO.builder()
                .missionList(missionList)
                .listSize(missionList.size())
                .pageNumber(memberMissionPage.getNumber())
                .pageSize(memberMissionPage.getSize())
                .totalPages(memberMissionPage.getTotalPages())
                .totalElements(memberMissionPage.getTotalElements())
                .first(memberMissionPage.isFirst())
                .last(memberMissionPage.isLast())
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
}

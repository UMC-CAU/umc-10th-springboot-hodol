package com.example.umc_study.domain.mission.controller;

import com.example.umc_study.domain.mission.dto.MissionResDTO;
import com.example.umc_study.domain.mission.enums.HomeMissionSortType;
import com.example.umc_study.domain.mission.enums.MissionStatus;
import com.example.umc_study.domain.mission.service.MissionService;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/home")
    public ApiResponse<MissionResDTO.HomeMissionListDTO> getHomeMissions(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "regionId") Long regionId,
            @RequestParam(name = "sortType", defaultValue = "LATEST") HomeMissionSortType sortType,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        MissionResDTO.HomeMissionListDTO result =
                missionService.getHomeMissions(memberId, regionId, sortType, pageable);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @GetMapping("/me")
    public ApiResponse<MissionResDTO.MissionListDTO> getMyMissions(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "status") MissionStatus status,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable
    ) {
        MissionResDTO.MissionListDTO result = missionService.getMyMissions(memberId, status, pageable);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PatchMapping("/{missionId}/complete")
    public ApiResponse<MissionResDTO.MissionCompleteResultDTO> completeMission(
            @PathVariable(name = "missionId") Long missionId,
            @RequestParam(name = "memberId") Long memberId
    ) {
        MissionResDTO.MissionCompleteResultDTO result = missionService.completeMission(missionId);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}

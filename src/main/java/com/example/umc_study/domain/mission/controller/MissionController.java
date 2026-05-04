package com.example.umc_study.domain.mission.controller;

import com.example.umc_study.domain.mission.dto.MissionReqDTO;
import com.example.umc_study.domain.mission.dto.MissionResDTO;
import com.example.umc_study.domain.mission.enums.MissionStatus;
import com.example.umc_study.domain.mission.exception.code.MissionSuccessCode;
import com.example.umc_study.domain.mission.service.MissionService;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.BaseSuccessCode;
import com.example.umc_study.global.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService missionService;

    @GetMapping("/me")
    public ApiResponse<MissionResDTO.MissionListDTO> getMyMissions(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "status") MissionStatus status
    ) {
        // Service에서 Enum 값에 따라 필터링된 결과를 가져옴
        MissionResDTO.MissionListDTO result = missionService.getMyMissions(status);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PatchMapping("/{missionId}/complete")
    public ApiResponse<MissionResDTO.MissionCompleteResultDTO> completeMission(
            @PathVariable(name = "missionId") Long missionId,
            @RequestParam(name = "memberId") Long memberId
    ) {
        // Service 단에서 해당 ID의 미션 상태를 'COMPLETED'로 변경하는 로직을 수행
        MissionResDTO.MissionCompleteResultDTO result = missionService.completeMission(missionId);

        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}

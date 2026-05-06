package com.example.umc_study.domain.mission.dto;

import lombok.Getter;
import lombok.Setter;

public class MissionReqDTO {

    // 1. 미션 도전하기(수락) 시 필요한 정보 (예시)

    public record ChallengeDTO (
        Long memberId // 어떤 유저가 도전하는지
    ){}

    // 2. 관리자가 미션을 새로 등록할 때 (예시)

    public record CreateMissionDTO (
        String title;       // 미션 제목
        String missionSpec; // 상세 내용
        Integer reward;     // 보상 포인트
        String deadline;    // 마감 기한 (LocalDate 등을 써도 됨)
    ){}
}

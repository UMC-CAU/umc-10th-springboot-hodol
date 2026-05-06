package com.example.umc_study.domain.mission.service;

import com.example.umc_study.domain.member.exception.MemberException;
import com.example.umc_study.domain.member.exception.code.MemberErrorCode;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.domain.mission.converter.MissionConverter;
import com.example.umc_study.domain.mission.dto.MissionResDTO;
import com.example.umc_study.domain.mission.entity.mapping.MemberMission;
import com.example.umc_study.domain.mission.enums.MissionStatus;
import com.example.umc_study.domain.mission.repository.MemberMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MemberMissionRepository memberMissionRepository;
    private final MemberRepository memberRepository;

    public MissionResDTO.MissionListDTO getMyMissions(Long memberId, MissionStatus status, Pageable pageable) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                getSort(status)
        );

        Page<MemberMission> memberMissionPage =
                memberMissionRepository.findAllByMember_IdAndStatus(memberId, status, sortedPageable);

        return MissionConverter.toMissionListDTO(memberMissionPage);
    }

    public MissionResDTO.MissionCompleteResultDTO completeMission(Long missionId) {
        return MissionResDTO.MissionCompleteResultDTO.builder()
                .missionId(missionId)
                .status("COMPLETED")
                .completedAt(LocalDateTime.now())
                .build();
    }

    private Sort getSort(MissionStatus status) {
        if (status == MissionStatus.COMPLETED) {
            return Sort.by(
                    Sort.Order.desc("updatedAt"),
                    Sort.Order.desc("id")
            );
        }

        return Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.desc("id")
        );
    }
}

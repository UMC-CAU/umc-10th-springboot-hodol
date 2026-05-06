package com.example.umc_study.domain.mission.repository;

import com.example.umc_study.domain.mission.entity.mapping.MemberMission;
import com.example.umc_study.domain.mission.enums.MissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberMissionRepository extends JpaRepository<MemberMission, Long> {

    @EntityGraph(attributePaths = {"mission", "mission.store"})
    Page<MemberMission> findAllByMember_IdAndStatus(Long memberId, MissionStatus status, Pageable pageable);

    long countByMember_IdAndStatus(Long memberId, MissionStatus status);
}

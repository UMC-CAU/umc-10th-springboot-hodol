package com.example.umc_study.domain.mission.repository;

import com.example.umc_study.domain.mission.entity.mapping.MemberMission;
import com.example.umc_study.domain.mission.enums.MissionStatus;
import com.example.umc_study.domain.mission.repository.projection.MemberMissionSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberMissionRepository extends JpaRepository<MemberMission, Long> {

    @EntityGraph(attributePaths = {"mission", "mission.store"})
    Page<MemberMission> findAllByMember_IdAndStatus(Long memberId, MissionStatus status, Pageable pageable);

    @Query(
            value = """
                    select
                        mm.id as memberMissionId,
                        m.id as missionId,
                        s.name as storeName,
                        m.missionSpec as missionSpec,
                        m.reward as reward,
                        mm.status as status
                    from MemberMission mm
                    join mm.mission m
                    join m.store s
                    where mm.member.id = :memberId
                      and mm.status = :status
                    """,
            countQuery = """
                    select count(mm)
                    from MemberMission mm
                    where mm.member.id = :memberId
                      and mm.status = :status
                    """
    )
    Page<MemberMissionSummaryProjection> findMissionSummariesByMemberIdAndStatus(
            @Param("memberId") Long memberId,
            @Param("status") MissionStatus status,
            Pageable pageable
    );

    long countByMember_IdAndStatus(Long memberId, MissionStatus status);
}

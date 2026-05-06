package com.example.umc_study.domain.mission.repository;

import com.example.umc_study.domain.mission.entity.Mission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    @EntityGraph(attributePaths = {"store", "store.location"})
    @Query("""
            select m
            from Mission m
            join m.store s
            join s.location l
            where l.id = :regionId
              and not exists (
                    select 1
                    from MemberMission mm
                    where mm.member.id = :memberId
                      and mm.mission = m
              )
            """)
    Page<Mission> findHomeMissions(
            @Param("memberId") Long memberId,
            @Param("regionId") Long regionId,
            Pageable pageable
    );
}

package com.example.umc_study.domain.mission.repository;

import com.example.umc_study.domain.mission.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}

package com.example.umc_study.domain.member.repository;

import com.example.umc_study.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    void deleteById(Long id);
}

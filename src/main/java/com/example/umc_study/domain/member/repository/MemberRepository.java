package com.example.umc_study.domain.member.repository;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);
    Optional<Member> findByEmail(String email);
    Optional<Member> findBySocialUid(String socialUid);
    Optional<Member> findBySocialTypeAndSocialUid(SocialType socialType, String socialUid);
    boolean existsByEmail(String email);
}

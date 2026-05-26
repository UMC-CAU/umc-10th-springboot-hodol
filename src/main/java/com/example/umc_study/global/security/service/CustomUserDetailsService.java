package com.example.umc_study.global.security.service;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.global.security.entity.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found: " + username));
        return new AuthMember(member);
    }
}

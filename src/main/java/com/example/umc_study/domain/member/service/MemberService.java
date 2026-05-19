package com.example.umc_study.domain.member.service;

import com.example.umc_study.domain.member.converter.MemberConverter;
import com.example.umc_study.domain.member.dto.MemberReqDTO;
import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.domain.member.dto.MyPageResponseDTO;
import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.Address;
import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.domain.member.exception.MemberException;
import com.example.umc_study.domain.member.exception.code.MemberErrorCode;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String createUser(

    ) {
        Member member = Member.builder()
                .name("test")
                .build();
        memberRepository.save(member);
        return "OK";
    }

    @Transactional
    public String deleteUser(

    ) {
        return "OK";
    }

    public String singleParameter(
            String singleParameter
    ){
        return singleParameter;
    }

    public MemberResDTO.RequestBody requestBody(
            MemberReqDTO.RequestBody dto
    ) {
        return MemberConverter.toRequestBody(dto.stringTest(), dto.longTest());
    }

    public MyPageResponseDTO getMyPage(
            MemberReqDTO.GetInfo dto
    ) {
        Long memberId = dto.id();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        long reviewCount = reviewRepository.countByMember_Id(memberId);
        return MemberConverter.toMyPageResponse(member, reviewCount);
    }

    @Transactional
    public MemberResDTO.JoinResultDTO joinMember(MemberReqDTO.JoinDTO request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
        }

        Member member = Member.builder()
                .name(request.name())
                .nickname(request.nickName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .profileUrl("https://example.com/profiles/default.png")
                .point(0)
                .gender(request.gender())
                .birth(request.birthDate())
                .address(Address.values()[0])
                .detailAddress("N/A")
                .socialUid("local:" + request.email())
                .socialType(SocialType.LOCAL)
                .build();

        Member savedMember = memberRepository.save(member);

        return MemberResDTO.JoinResultDTO.builder()
                .memberId(savedMember.getId())
                .createdAt(savedMember.getCreatedAt())
                .build();
    }
}

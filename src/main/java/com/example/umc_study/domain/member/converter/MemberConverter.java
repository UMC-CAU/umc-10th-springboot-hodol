package com.example.umc_study.domain.member.converter;

import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.domain.member.dto.MyPageResponseDTO;
import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.Address;
import com.example.umc_study.domain.member.enums.Gender;
import com.example.umc_study.global.security.dto.OAuthDTO;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

public class MemberConverter {

    public static MemberResDTO.RequestBody toRequestBody(
            String stringTest,
            Long longTest
    ){
        return MemberResDTO.RequestBody.builder()
                .stringTest(stringTest)
                .longTest(longTest)
                .build();
    }

    public static MemberResDTO.GetInfo toGetInfo(
            Member member
    ){
        return MemberResDTO.GetInfo.builder()
                .email(member.getEmail())
                .name(member.getName())
                .point(member.getPoint())
                .phoneNumber(member.getPhoneNumber())
                .profileUrl(member.getProfileUrl())
                .build();
    }

    public static MyPageResponseDTO toMyPageResponse(Member member, long reviewCount) {
        return new MyPageResponseDTO(
                new MyPageResponseDTO.ProfileInfo(
                        member.getNickname(),
                        member.getEmail(),
                        new MyPageResponseDTO.PhoneInfo(
                                member.getPhoneNumber(),
                                StringUtils.hasText(member.getPhoneNumber())
                        )
                ),
                new MyPageResponseDTO.ActivitySummary(
                        member.getPoint(),
                        reviewCount
                )
        );
    }

    public static Member toMember(OAuthDTO dto) {
        return Member.builder()
                .name(dto.getName())
                .email(dto.getSocialEmail())
                .nickname(dto.getName())
                .password(dto.getSocialUid())
                .phoneNumber(null)
                .profileUrl("https://example.com/profiles/default.png")
                .point(0)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2000, 1, 1))
                .address(Address.values()[0])
                .detailAddress("N/A")
                .socialUid(dto.getSocialUid())
                .socialType(dto.getSocialType())
                .build();
    }
}

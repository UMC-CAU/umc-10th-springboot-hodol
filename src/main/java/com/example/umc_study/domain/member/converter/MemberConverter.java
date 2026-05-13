package com.example.umc_study.domain.member.converter;

import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.domain.member.dto.MyPageResponseDTO;
import com.example.umc_study.domain.member.entity.Member;
import org.springframework.util.StringUtils;

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
                        member.getName(),
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
}

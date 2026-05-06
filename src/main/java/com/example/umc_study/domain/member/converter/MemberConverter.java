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
        return MyPageResponseDTO.builder()
                .profile(
                        MyPageResponseDTO.ProfileInfo.builder()
                                .nickname(member.getName())
                                .email(member.getEmail())
                                .phoneInfo(
                                        MyPageResponseDTO.PhoneInfo.builder()
                                                .phoneNumber(member.getPhoneNumber())
                                                .verified(StringUtils.hasText(member.getPhoneNumber()))
                                                .build()
                                )
                                .build()
                )
                .activitySummary(
                        MyPageResponseDTO.ActivitySummary.builder()
                                .currentPointBalance(member.getPoint())
                                .reviewCount(reviewCount)
                                .build()
                )
                .build();
    }
}

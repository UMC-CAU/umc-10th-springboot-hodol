package com.example.umc_study.global.security.dto;

import com.example.umc_study.domain.member.enums.SocialType;

public interface OAuthDTO {
    SocialType getSocialType();
    String getSocialUid();
    String getSocialEmail();
    String getName();
}

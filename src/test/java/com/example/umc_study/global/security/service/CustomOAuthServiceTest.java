package com.example.umc_study.global.security.service;

import com.example.umc_study.domain.member.exception.MemberException;
import com.example.umc_study.domain.member.exception.code.MemberErrorCode;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.global.security.dto.OAuthDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class CustomOAuthServiceTest {

    private final CustomOAuthService service = new CustomOAuthService(mock(MemberRepository.class));

    @Test
    @DisplayName("kakao dto falls back to generated email when account email is missing")
    void toOAuthDtoFallsBackToGeneratedEmail() {
        OAuthDTO dto = service.toOAuthDto(
                "12345",
                com.example.umc_study.domain.member.enums.SocialType.KAKAO,
                Map.of("profile", Map.of("nickname", "kakao-user"))
        );

        assertThat(dto.getSocialEmail()).isEqualTo("kakao_12345@social.local");
        assertThat(dto.getName()).isEqualTo("kakao-user");
    }

    @Test
    @DisplayName("kakao dto falls back to generated nickname when profile nickname is missing")
    void toOAuthDtoFallsBackToGeneratedNickname() {
        OAuthDTO dto = service.toOAuthDto(
                "12345",
                com.example.umc_study.domain.member.enums.SocialType.KAKAO,
                Map.of()
        );

        assertThat(dto.getSocialEmail()).isEqualTo("kakao_12345@social.local");
        assertThat(dto.getName()).isEqualTo("kakao_12345");
    }

    @Test
    @DisplayName("unsupported provider throws member exception")
    void toOAuthDtoRejectsUnsupportedProvider() {
        assertThatThrownBy(() -> service.toOAuthDto(
                "12345",
                com.example.umc_study.domain.member.enums.SocialType.GOOGLE,
                Map.of()
        ))
                .isInstanceOf(MemberException.class)
                .extracting("errorCode")
                .isEqualTo(MemberErrorCode.NOT_SUPPORT_SOCIAL_PROVIDER);
    }

    @Test
    @DisplayName("social uid resolves from kakao numeric id attribute")
    void resolveSocialUidFromNumericIdAttribute() {
        assertThat(service.resolveSocialUid(12345L)).isEqualTo("12345");
    }
}

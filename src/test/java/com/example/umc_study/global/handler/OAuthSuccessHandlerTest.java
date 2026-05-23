package com.example.umc_study.global.handler;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.Address;
import com.example.umc_study.domain.member.enums.Gender;
import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.global.security.entity.OAuthMember;
import com.example.umc_study.global.security.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthSuccessHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("oauth success handler writes jwt login response from authentication principal")
    void onAuthenticationSuccessWritesJwtJson() throws Exception {
        JwtUtil jwtUtil = new JwtUtil(
                "test-secret-key-that-is-long-enough-for-hmac-signing-123456",
                1800000L
        );
        OAuthSuccessHandler handler = new OAuthSuccessHandler(jwtUtil);

        Member member = Member.builder()
                .id(1L)
                .name("kakao-user")
                .nickname("kakao-nickname")
                .email("kakao-user@example.com")
                .password("social-password")
                .phoneNumber("01012341234")
                .profileUrl("https://example.com/kakao-profile.png")
                .point(0)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2000, 1, 1))
                .address(Address.values()[0])
                .detailAddress("101-1001")
                .socialUid("123456789")
                .socialType(SocialType.KAKAO)
                .build();

        OAuthMember principal = new OAuthMember(member, Map.of("id", 123456789L));
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of()
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.onAuthenticationSuccess(request, response, authentication);

        JsonNode json = objectMapper.readTree(response.getContentAsByteArray());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(json.path("isSuccess").asBoolean()).isTrue();
        assertThat(json.path("result").path("accessToken").asText()).isNotBlank();
        assertThat(json.path("result").path("memberId").asLong()).isEqualTo(1L);
        assertThat(json.path("result").path("email").asText()).isEqualTo("kakao-user@example.com");
        assertThat(json.path("result").path("nickname").asText()).isEqualTo("kakao-nickname");
    }
}

package com.example.umc_study.global.security.service;

import com.example.umc_study.domain.member.converter.MemberConverter;
import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.domain.member.exception.MemberException;
import com.example.umc_study.domain.member.exception.code.MemberErrorCode;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.global.security.dto.KakaoDTO;
import com.example.umc_study.global.security.dto.OAuthDTO;
import com.example.umc_study.global.security.entity.OAuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuthService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuthMember = super.loadUser(userRequest);

        SocialType providerId;
        String socialUid;
        Map<String, Object> attributes = getAttributesMap(oAuthMember.getAttribute("kakao_account"));

        try {
            providerId = SocialType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
            socialUid = resolveSocialUid(oAuthMember.getAttribute("id"));
        } catch (IllegalArgumentException e) {
            throw new MemberException(MemberErrorCode.NOT_SUPPORT_SOCIAL_PROVIDER);
        }

        OAuthDTO dto = toOAuthDto(socialUid, providerId, attributes);

        Member member = memberRepository.findBySocialTypeAndSocialUid(providerId, socialUid)
                .orElseGet(() -> {
                    Member newMember = MemberConverter.toMember(dto);
                    memberRepository.save(newMember);
                    return newMember;
                });

        return new OAuthMember(member, oAuthMember.getAttributes());
    }

    OAuthDTO toOAuthDto(String socialUid, SocialType providerId, Map<String, Object> attributes) {
        return switch (providerId) {
            case KAKAO -> {
                Map<String, Object> profile = getAttributesMap(attributes.get("profile"));
                String email = getString(attributes, "email");
                String nickname = getString(profile, "nickname");

                String resolvedEmail = StringUtils.hasText(email)
                        ? email
                        : "kakao_" + socialUid + "@social.local";
                String resolvedNickname = StringUtils.hasText(nickname)
                        ? nickname
                        : "kakao_" + socialUid;

                yield new KakaoDTO(socialUid, resolvedEmail, resolvedNickname);
            }
            default -> throw new MemberException(MemberErrorCode.NOT_SUPPORT_SOCIAL_PROVIDER);
        };
    }

    private Map<String, Object> getAttributesMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Collections.emptyMap();
    }

    private String getString(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        return value == null ? null : value.toString();
    }

    String resolveSocialUid(Object value) {
        return value == null ? null : value.toString();
    }
}

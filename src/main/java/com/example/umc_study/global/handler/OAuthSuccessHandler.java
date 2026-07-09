package com.example.umc_study.global.handler;

import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.BaseSuccessCode;
import com.example.umc_study.global.code.GeneralSuccessCode;
import com.example.umc_study.global.security.entity.AuthMember;
import com.example.umc_study.global.security.entity.OAuthMember;
import com.example.umc_study.global.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        BaseSuccessCode code = GeneralSuccessCode.OK;

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code.getStatus().value());

        OAuthMember member = (OAuthMember) authentication.getPrincipal();
        String accessToken = jwtUtil.createAccessToken(new AuthMember(member.getMember()));

        ApiResponse<MemberResDTO.LoginResultDTO> responseBody = ApiResponse.onSuccess(
                code,
                MemberResDTO.LoginResultDTO.builder()
                        .accessToken(accessToken)
                        .memberId(member.getMember().getId())
                        .email(member.getMember().getEmail())
                        .nickname(member.getMember().getNickname())
                        .build()
        );

        objectMapper.writeValue(response.getOutputStream(), responseBody);
    }
}

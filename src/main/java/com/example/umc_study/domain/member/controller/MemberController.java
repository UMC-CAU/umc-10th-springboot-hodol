package com.example.umc_study.domain.member.controller;

import com.example.umc_study.domain.member.dto.MemberReqDTO;
import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.domain.member.dto.MyPageResponseDTO;
import com.example.umc_study.domain.member.service.MemberService;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.GeneralSuccessCode;
import com.example.umc_study.global.security.entity.AuthMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/v2/users/me")
    public ApiResponse<MyPageResponseDTO> getMyPage(
            @AuthenticationPrincipal AuthMember authMember
    ) {
        MyPageResponseDTO result = memberService.getMyPage(authMember);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PostMapping("/signup")
    public ApiResponse<MemberResDTO.JoinResultDTO> join(
            @Valid @RequestBody MemberReqDTO.JoinDTO request
    ) {
        MemberResDTO.JoinResultDTO result = memberService.joinMember(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PostMapping("/login")
    public ApiResponse<MemberResDTO.LoginResultDTO> login(
            @Valid @RequestBody MemberReqDTO.LoginDTO request
    ) {
        MemberResDTO.LoginResultDTO result = memberService.login(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}

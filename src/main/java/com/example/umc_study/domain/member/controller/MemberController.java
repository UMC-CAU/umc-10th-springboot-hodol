package com.example.umc_study.domain.member.controller;

import com.example.umc_study.domain.member.dto.MemberReqDTO;
import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.domain.member.dto.MyPageResponseDTO;
import com.example.umc_study.domain.member.service.MemberService;
import com.example.umc_study.global.apiPayload.ApiResponse;
import com.example.umc_study.global.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/v1/users/me")
    public ApiResponse<MyPageResponseDTO> getMyPage(
            @RequestBody MemberReqDTO.GetInfo dto
    ){
        MyPageResponseDTO result = memberService.getMyPage(dto);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    @PostMapping("/signup")
    public ApiResponse<MemberResDTO.JoinResultDTO> join(
            @RequestBody MemberReqDTO.JoinDTO request
    ) {
        MemberResDTO.JoinResultDTO result = memberService.joinMember(request);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }
}

package com.example.umc_study.domain.member.controller;

import com.example.umc_study.domain.member.dto.MemberReqDTO;
import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.domain.member.exception.MemberException;
import com.example.umc_study.domain.member.exception.code.MemberErrorCode;
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

    //마이페이지
    @PostMapping("/v1/users/me")
    public ApiResponse<MemberResDTO.GetInfo> getInfo(
            @RequestBody MemberReqDTO.GetInfo dto
    ){

        MemberResDTO.GetInfo result = memberService.getInfo(dto);
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ApiResponse<MemberResDTO.JoinResultDTO> join(
            @RequestBody MemberReqDTO.JoinDTO request
    ) {
        // Service에서 유저를 저장하고 결과를 받아옴
        MemberResDTO.JoinResultDTO result = memberService.joinMember(request);

        // 성공 응답 반환 (GeneralSuccessCode.OK 사용)
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, result);
    }

}

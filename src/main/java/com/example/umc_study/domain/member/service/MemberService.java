package com.example.umc_study.domain.member.service;

import com.example.umc_study.domain.member.converter.MemberConverter;
import com.example.umc_study.domain.member.dto.MemberReqDTO;
import com.example.umc_study.domain.member.dto.MemberResDTO;
import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.exception.MemberException;
import com.example.umc_study.domain.member.exception.code.MemberErrorCode;
import com.example.umc_study.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public String createUser(

    ) {
        Member member = Member.builder()
                .name("test")
                .build();
        memberRepository.save(member);
        return "OK";
    }

    @Transactional
    public String deleteUser(

    ) {
        // memberRepository.deleteByName("test");
        return "OK";
    }

    // Query Parameter
    public String singleParameter(
            String singleParameter
    ){
        return singleParameter;
    }

    // Request Body
    public MemberResDTO.RequestBody requestBody(
            MemberReqDTO.RequestBody dto
    ) {
        return MemberConverter.toRequestBody(dto.stringTest(), dto.longTest());
    }

    // 마이페이지
    public MemberResDTO.GetInfo getInfo(
            MemberReqDTO.GetInfo dto
    ) {
        // DTO에서 유저 ID를 추출
        Long memberId = dto.id();

        // DB에서 해당 유저 ID로 데이터 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 컨버터를 이용해서 응답 DTO 생성 & return
        return MemberConverter.toGetInfo(member);
    }

    @Transactional
    public MemberResDTO.JoinResultDTO joinMember(MemberReqDTO.JoinDTO request) {
        // TODO: 구현 필요. 현재는 컴파일 에러를 막기 위한 더미 반환
        return MemberResDTO.JoinResultDTO.builder()
                .memberId(1L)
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}

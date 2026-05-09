package com.example.umc_study.domain.member.controller;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.Address;
import com.example.umc_study.domain.member.enums.Gender;
import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.domain.mission.entity.Store;
import com.example.umc_study.domain.mission.repository.StoreRepository;
import com.example.umc_study.domain.review.entity.Review;
import com.example.umc_study.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("my page returns profile and activity summary")
    void getMyPage() throws Exception {
        Member member = memberRepository.save(createMember());
        Store store = storeRepository.save(createStore());

        reviewRepository.save(createReview(member, store, "First review"));
        reviewRepository.save(createReview(member, store, "Second review"));

        String requestBody = """
                {
                  "id": %d
                }
                """.formatted(member.getId());

        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.profile.nickname").value("my-nickname"))
                .andExpect(jsonPath("$.result.profile.email").value("mypage@example.com"))
                .andExpect(jsonPath("$.result.profile.phoneInfo.phoneNumber").value("01099998888"))
                .andExpect(jsonPath("$.result.profile.phoneInfo.verified").value(true))
                .andExpect(jsonPath("$.result.activitySummary.currentPointBalance").value(1200))
                .andExpect(jsonPath("$.result.activitySummary.reviewCount").value(2));
    }

    @Test
    @DisplayName("my page returns not found when member does not exist")
    void getMyPageFailsWhenMemberMissing() throws Exception {
        String requestBody = """
                {
                  "id": 999999
                }
                """;

        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("MEMBER404_1"));
    }

    @Test
    @DisplayName("my page returns bad request when member id is missing")
    void getMyPageFailsWhenMemberIdMissing() throws Exception {
        String requestBody = """
                {
                }
                """;

        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("MEMBER400_1"));
    }

    @Test
    @DisplayName("my page returns bad request when member id is not positive")
    void getMyPageFailsWhenMemberIdIsInvalid() throws Exception {
        String requestBody = """
                {
                  "id": 0
                }
                """;

        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("MEMBER400_2"));
    }

    @Test
    @DisplayName("signup returns bad request when email format is invalid")
    void joinFailsWhenEmailFormatIsInvalid() throws Exception {
        String requestBody = """
                {
                  "name": "tester",
                  "password": "secret",
                  "age": 24,
                  "email": "invalid-email",
                  "gender": "FEMALE",
                  "nickName": "tester-nickname",
                  "phoneNumber": "01012345678",
                  "birthDate": "2001-01-01"
                }
                """;

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("MEMBER400_8"));
    }

    private Member createMember() {
        return Member.builder()
                .name("my-nickname")
                .email("mypage@example.com")
                .phoneNumber("01099998888")
                .profileUrl("https://example.com/profile.png")
                .point(1200)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(1999, 5, 6))
                .address(Address.values()[0])
                .detailAddress("102-1203")
                .socialUid("mypage-uid")
                .socialType(SocialType.KAKAO)
                .build();
    }

    private Store createStore() {
        return Store.builder()
                .name("UMC Burger")
                .address("Seoul")
                .score(4.5f)
                .build();
    }

    private Review createReview(Member member, Store store, String title) {
        return Review.builder()
                .title(title)
                .score(4.5f)
                .body(title + " body")
                .member(member)
                .store(store)
                .build();
    }
}

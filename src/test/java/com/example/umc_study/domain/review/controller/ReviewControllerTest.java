package com.example.umc_study.domain.review.controller;

import com.example.umc_study.domain.member.entity.Member;
import com.example.umc_study.domain.member.enums.Address;
import com.example.umc_study.domain.member.enums.Gender;
import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.domain.member.repository.MemberRepository;
import com.example.umc_study.domain.mission.entity.Store;
import com.example.umc_study.domain.mission.repository.StoreRepository;
import com.example.umc_study.domain.review.entity.Review;
import com.example.umc_study.domain.review.repository.ReviewRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("create review stores review and empty reply")
    void createReviewSuccess() throws Exception {
        Member member = memberRepository.save(createMember());
        Store store = storeRepository.save(createStore());

        String requestBody = """
                {
                  "memberId": %d,
                  "score": 4.5,
                  "body": "Fresh ingredients and quick service."
                }
                """.formatted(member.getId());

        String responseBody = mockMvc.perform(post("/api/stores/{storeId}/reviews", store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("REVIEW201"))
                .andExpect(jsonPath("$.result.reviewId").isNumber())
                .andExpect(jsonPath("$.result.createdAt").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JsonNode responseJson = objectMapper.readTree(responseBody);
        Long reviewId = responseJson.path("result").path("reviewId").asLong();

        Review savedReview = reviewRepository.findById(reviewId).orElseThrow();
        assertThat(savedReview.getCreatedAt()).isNotNull();
        assertThat(savedReview.getMember().getId()).isEqualTo(member.getId());
        assertThat(savedReview.getStore().getId()).isEqualTo(store.getId());
        assertThat(savedReview.getScore()).isEqualTo(4.5f);
        assertThat(savedReview.getBody()).isEqualTo("Fresh ingredients and quick service.");
        assertThat(savedReview.getReply()).isNotNull();
        assertThat(savedReview.getReply().getId()).isNotNull();
        assertThat(savedReview.getReply().getBody()).isEmpty();
    }

    @Test
    @DisplayName("create review returns not found when member does not exist")
    void createReviewFailsWhenMemberIsMissing() throws Exception {
        Store store = storeRepository.save(createStore());

        String requestBody = """
                {
                  "memberId": 999999,
                  "score": 4.0,
                  "body": "Still should fail."
                }
                """;

        mockMvc.perform(post("/api/stores/{storeId}/reviews", store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("REVIEW404_1"));
    }

    private Member createMember() {
        return Member.builder()
                .name("reviewer")
                .email("reviewer@example.com")
                .phoneNumber("01012345678")
                .profileUrl("https://example.com/profile.png")
                .point(0)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2000, 1, 1))
                .address(Address.values()[0])
                .detailAddress("101-1001")
                .socialUid("social-uid-1")
                .socialType(SocialType.KAKAO)
                .build();
    }

    private Store createStore() {
        return Store.builder()
                .name("UMC Kitchen")
                .address("Seoul")
                .score(4.2f)
                .build();
    }
}

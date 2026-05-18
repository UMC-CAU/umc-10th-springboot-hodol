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
                .andExpect(jsonPath("$.code").value("COMMON200_1"))
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

    @Test
    @DisplayName("create review returns bad request when body is blank")
    void createReviewFailsWhenBodyIsBlank() throws Exception {
        Member member = memberRepository.save(createMember());
        Store store = storeRepository.save(createStore());

        String requestBody = """
                {
                  "memberId": %d,
                  "score": 4.0,
                  "body": "   "
                }
                """.formatted(member.getId());

        mockMvc.perform(post("/api/stores/{storeId}/reviews", store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON400_1"));
    }

    @Test
    @DisplayName("create review returns bad request when member id is not positive")
    void createReviewFailsWhenMemberIdIsInvalid() throws Exception {
        Store store = storeRepository.save(createStore());

        String requestBody = """
                {
                  "memberId": 0,
                  "score": 4.0,
                  "body": "Still should fail."
                }
                """;

        mockMvc.perform(post("/api/stores/{storeId}/reviews", store.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON400_1"));
    }

    @Test
    @DisplayName("get my reviews supports id cursor pagination")
    void getMyReviewsByIdCursor() throws Exception {
        Member member = memberRepository.save(createMember("reviewer-id", "reviewer-id@example.com", "social-uid-id"));
        Member otherMember = memberRepository.save(createMember("other-reviewer", "other-reviewer@example.com", "social-uid-other"));
        Store firstStore = storeRepository.save(createStore("UMC Kitchen"));
        Store secondStore = storeRepository.save(createStore("UMC Cafe"));

        Review firstReview = reviewRepository.save(createReview(member, firstStore, "First review", 3.5f, "First body"));
        Review secondReview = reviewRepository.save(createReview(member, secondStore, "Second review", 4.0f, "Second body"));
        Review thirdReview = reviewRepository.save(createReview(member, firstStore, "Third review", 4.5f, "Third body"));
        reviewRepository.save(createReview(otherMember, firstStore, "Other review", 5.0f, "Other body"));

        String firstRequestBody = """
                {
                  "memberId": %d,
                  "size": 2,
                  "sortType": "ID"
                }
                """.formatted(member.getId());

        mockMvc.perform(post("/api/reviews/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("COMMON200_1"))
                .andExpect(jsonPath("$.result.reviewList[0].reviewId").value(thirdReview.getId()))
                .andExpect(jsonPath("$.result.reviewList[0].storeName").value("UMC Kitchen"))
                .andExpect(jsonPath("$.result.reviewList[1].reviewId").value(secondReview.getId()))
                .andExpect(jsonPath("$.result.pagination.nextCursorId").value(secondReview.getId()))
                .andExpect(jsonPath("$.result.pagination.nextCursorScore").isEmpty())
                .andExpect(jsonPath("$.result.pagination.hasNext").value(true));

        String secondRequestBody = """
                {
                  "memberId": %d,
                  "cursorId": %d,
                  "size": 2,
                  "sortType": "ID"
                }
                """.formatted(member.getId(), secondReview.getId());

        mockMvc.perform(post("/api/reviews/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.reviewList[0].reviewId").value(firstReview.getId()))
                .andExpect(jsonPath("$.result.pagination.hasNext").value(false));
    }

    @Test
    @DisplayName("get my reviews supports score cursor pagination")
    void getMyReviewsByScoreCursor() throws Exception {
        Member member = memberRepository.save(createMember("reviewer-score", "reviewer-score@example.com", "social-uid-score"));
        Store store = storeRepository.save(createStore("UMC Score"));

        Review olderTieReview = reviewRepository.save(createReview(member, store, "Older tie", 4.5f, "Older tie body"));
        Review topReview = reviewRepository.save(createReview(member, store, "Top review", 5.0f, "Top review body"));
        Review newerTieReview = reviewRepository.save(createReview(member, store, "Newer tie", 4.5f, "Newer tie body"));
        Review lowReview = reviewRepository.save(createReview(member, store, "Low review", 3.0f, "Low review body"));

        String firstRequestBody = """
                {
                  "memberId": %d,
                  "size": 2,
                  "sortType": "SCORE"
                }
                """.formatted(member.getId());

        mockMvc.perform(post("/api/reviews/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.reviewList[0].reviewId").value(topReview.getId()))
                .andExpect(jsonPath("$.result.reviewList[0].score").value(5.0))
                .andExpect(jsonPath("$.result.reviewList[1].reviewId").value(newerTieReview.getId()))
                .andExpect(jsonPath("$.result.pagination.nextCursorId").value(newerTieReview.getId()))
                .andExpect(jsonPath("$.result.pagination.nextCursorScore").value(4.5))
                .andExpect(jsonPath("$.result.pagination.hasNext").value(true));

        String secondRequestBody = """
                {
                  "memberId": %d,
                  "cursorId": %d,
                  "cursorScore": 4.5,
                  "size": 2,
                  "sortType": "SCORE"
                }
                """.formatted(member.getId(), newerTieReview.getId());

        mockMvc.perform(post("/api/reviews/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.reviewList[0].reviewId").value(olderTieReview.getId()))
                .andExpect(jsonPath("$.result.reviewList[1].reviewId").value(lowReview.getId()))
                .andExpect(jsonPath("$.result.pagination.hasNext").value(false));
    }

    @Test
    @DisplayName("get my reviews returns bad request when score cursor is incomplete")
    void getMyReviewsFailsWhenScoreCursorIsIncomplete() throws Exception {
        Member member = memberRepository.save(createMember());

        String requestBody = """
                {
                  "memberId": %d,
                  "cursorScore": 4.5,
                  "size": 2,
                  "sortType": "SCORE"
                }
                """.formatted(member.getId());

        mockMvc.perform(post("/api/reviews/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("REVIEW400_4"));
    }

    @Test
    @DisplayName("get my reviews returns bad request when size is not positive")
    void getMyReviewsFailsWhenSizeIsInvalid() throws Exception {
        Member member = memberRepository.save(createMember());

        String requestBody = """
                {
                  "memberId": %d,
                  "size": 0,
                  "sortType": "ID"
                }
                """.formatted(member.getId());

        mockMvc.perform(post("/api/reviews/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON400_1"));
    }

    private Member createMember() {
        return createMember("reviewer", "reviewer@example.com", "social-uid-1");
    }

    private Member createMember(String name, String email, String socialUid) {
        return Member.builder()
                .name(name)
                .email(email)
                .phoneNumber("01012345678")
                .profileUrl("https://example.com/profile.png")
                .point(0)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2000, 1, 1))
                .address(Address.values()[0])
                .detailAddress("101-1001")
                .socialUid(socialUid)
                .socialType(SocialType.KAKAO)
                .build();
    }

    private Store createStore() {
        return createStore("UMC Kitchen");
    }

    private Store createStore(String name) {
        return Store.builder()
                .name(name)
                .address("Seoul")
                .score(4.2f)
                .build();
    }

    private Review createReview(Member member, Store store, String title, float score, String body) {
        return Review.builder()
                .title(title)
                .score(score)
                .body(body)
                .member(member)
                .store(store)
                .build();
    }
}

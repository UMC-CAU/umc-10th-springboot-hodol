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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                        .with(user("mypage@example.com"))
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
                        .with(user("missing-member@example.com"))
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
                        .with(user("missing-id@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON400_1"));
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
                        .with(user("invalid-id@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON400_1"));
    }

    @Test
    @DisplayName("my page returns unauthorized when authentication is missing")
    void getMyPageFailsWhenAuthenticationIsMissing() throws Exception {
        String requestBody = """
                {
                  "id": 1
                }
                """;

        mockMvc.perform(post("/api/v1/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON401_1"));
    }

    @Test
    @DisplayName("login page returns html form with email and password fields")
    void loginPageLoads() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"email\"")))
                .andExpect(content().string(containsString("name=\"password\"")))
                .andExpect(content().string(containsString("form method=\"post\" action=\"/login\"")));
    }

    @Test
    @DisplayName("signup stores encoded password and returns created member info")
    void joinSuccess() throws Exception {
        String requestBody = """
                {
                  "name": "tester",
                  "password": "secret-password",
                  "age": 24,
                  "email": "tester@example.com",
                  "gender": "FEMALE",
                  "nickName": "tester-nickname",
                  "phoneNumber": "01012345678",
                  "birthDate": "2001-01-01"
                }
                """;

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.memberId").isNumber())
                .andExpect(jsonPath("$.result.createdAt").isNotEmpty());

        Member savedMember = memberRepository.findByEmail("tester@example.com")
                .orElseThrow();

        assertThat(savedMember.getNickname()).isEqualTo("tester-nickname");
        assertThat(savedMember.getPassword()).isNotEqualTo("secret-password");
        assertThat(passwordEncoder.matches("secret-password", savedMember.getPassword())).isTrue();
        assertThat(savedMember.getSocialType()).isEqualTo(SocialType.LOCAL);
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
                .andExpect(jsonPath("$.code").value("COMMON400_1"));
    }

    @Test
    @DisplayName("signup returns conflict when email already exists")
    void joinFailsWhenEmailAlreadyExists() throws Exception {
        memberRepository.save(createLocalMember("duplicate@example.com", "duplicate-nickname"));

        String requestBody = """
                {
                  "name": "tester",
                  "password": "secret",
                  "age": 24,
                  "email": "duplicate@example.com",
                  "gender": "FEMALE",
                  "nickName": "tester-nickname",
                  "phoneNumber": "01012345678",
                  "birthDate": "2001-01-01"
                }
                """;

        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("MEMBER409_1"));
    }

    private Member createMember() {
        return Member.builder()
                .name("my-page-user")
                .nickname("my-nickname")
                .email("mypage@example.com")
                .password("encoded-password")
                .phoneNumber("01099998888")
                .profileUrl("https://example.com/profile.png")
                .point(1200)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(1999, 5, 6))
                .address(Address.values()[0])
                .detailAddress("102-1203")
                .socialUid("local:mypage@example.com")
                .socialType(SocialType.LOCAL)
                .build();
    }

    private Member createLocalMember(String email, String nickname) {
        return Member.builder()
                .name("existing-user")
                .nickname(nickname)
                .email(email)
                .password(passwordEncoder.encode("already-encoded"))
                .phoneNumber("01011112222")
                .profileUrl("https://example.com/profile.png")
                .point(0)
                .gender(Gender.FEMALE)
                .birth(LocalDate.of(2000, 1, 1))
                .address(Address.values()[0])
                .detailAddress("101-1001")
                .socialUid("local:" + email)
                .socialType(SocialType.LOCAL)
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

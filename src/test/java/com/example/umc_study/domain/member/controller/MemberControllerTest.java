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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("my page returns profile and activity summary when bearer token is valid")
    void getMyPage() throws Exception {
        Member member = memberRepository.save(createLocalMember("mypage@example.com", "my-nickname"));
        Store store = storeRepository.save(createStore());

        reviewRepository.save(createReview(member, store, "First review"));
        reviewRepository.save(createReview(member, store, "Second review"));

        String accessToken = loginAndGetAccessToken("mypage@example.com", "already-encoded");

        mockMvc.perform(get("/api/v2/users/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.profile.nickname").value("my-nickname"))
                .andExpect(jsonPath("$.result.profile.email").value("mypage@example.com"))
                .andExpect(jsonPath("$.result.profile.phoneInfo.phoneNumber").value("01011112222"))
                .andExpect(jsonPath("$.result.profile.phoneInfo.verified").value(true))
                .andExpect(jsonPath("$.result.activitySummary.currentPointBalance").value(0))
                .andExpect(jsonPath("$.result.activitySummary.reviewCount").value(2));
    }

    @Test
    @DisplayName("my page returns unauthorized when bearer token is missing")
    void getMyPageFailsWhenAuthenticationIsMissing() throws Exception {
        mockMvc.perform(get("/api/v2/users/me"))
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
    @DisplayName("login page exposes kakao oauth entry link")
    void loginPageShowsKakaoEntry() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/oauth/authorize/kakao")));
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
    @DisplayName("login returns access token and member info when credentials are valid")
    void loginSuccess() throws Exception {
        memberRepository.save(createLocalMember("login-user@example.com", "login-nickname"));

        String requestBody = """
                {
                  "email": "login-user@example.com",
                  "password": "already-encoded"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.result.memberId").isNumber())
                .andExpect(jsonPath("$.result.email").value("login-user@example.com"))
                .andExpect(jsonPath("$.result.nickname").value("login-nickname"));
    }

    @Test
    @DisplayName("login returns unauthorized when password is invalid")
    void loginFailsWhenPasswordIsInvalid() throws Exception {
        memberRepository.save(createLocalMember("wrong-password@example.com", "wrong-password-user"));

        String requestBody = """
                {
                  "email": "wrong-password@example.com",
                  "password": "not-the-right-password"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON401_1"));
    }

    @Test
    @DisplayName("login returns unauthorized when email does not exist")
    void loginFailsWhenEmailDoesNotExist() throws Exception {
        String requestBody = """
                {
                  "email": "missing-login@example.com",
                  "password": "some-password"
                }
                """;

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.code").value("COMMON401_1"));
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

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        String requestBody = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.path("result").path("accessToken").asText();
    }
}

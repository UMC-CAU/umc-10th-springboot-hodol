# Optional Kakao OAuth Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 카카오 OAuth 로그인 후 JWT를 JSON으로 발급하고 로그인 페이지에서 카카오 로그인 진입점을 노출한다.

**Architecture:** 스프링 시큐리티의 OAuth2 클라이언트 흐름을 유지하되, 사용자 정보 적재는 `CustomOAuthService`, JWT 응답은 `OAuthSuccessHandler`가 담당한다. 기존 JWT 필터와 마이페이지 API는 그대로 재사용한다.

**Tech Stack:** Spring Boot, Spring Security, OAuth2 Client, JWT, MockMvc, JUnit 5

---

### Task 1: Add Failing Optional OAuth Tests

**Files:**
- Modify: `src/test/java/com/example/umc_study/domain/member/controller/MemberControllerTest.java`
- Create: `src/test/java/com/example/umc_study/global/handler/OAuthSuccessHandlerTest.java`

- [ ] **Step 1: Write the failing login page test**

```java
@Test
@DisplayName("login page exposes kakao oauth entry link")
void loginPageShowsKakaoEntry() throws Exception {
    mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("/oauth/authorize/kakao")));
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat test --tests "*MemberControllerTest.loginPageShowsKakaoEntry"`
Expected: FAIL because the current login page does not contain the Kakao link.

- [ ] **Step 3: Write the failing OAuth success handler test**

```java
@Test
void onAuthenticationSuccessWritesJwtJson() throws Exception {
    Member member = createKakaoMember();
    OAuthMember principal = new OAuthMember(member, Map.of("id", "123"));
    Authentication authentication =
            new UsernamePasswordAuthenticationToken(principal, null, List.of());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    handler.onAuthenticationSuccess(request, response, authentication);

    JsonNode json = objectMapper.readTree(response.getContentAsByteArray());
    assertThat(json.path("result").path("accessToken").asText()).isNotBlank();
}
```

- [ ] **Step 4: Run test to verify it fails**

Run: `.\gradlew.bat test --tests "*OAuthSuccessHandlerTest"`
Expected: FAIL until production behavior and assertions align.

### Task 2: Implement Kakao Entry UI and Success Response

**Files:**
- Modify: `src/main/java/com/example/umc_study/global/security/controller/LoginController.java`
- Modify: `src/main/java/com/example/umc_study/global/handler/OAuthSuccessHandler.java`

- [ ] **Step 1: Add Kakao login entry to `/login` page**

```java
<a href="/oauth/authorize/kakao">Continue with Kakao</a>
```

- [ ] **Step 2: Use the authenticated OAuth principal directly in the success handler**

```java
OAuthMember member = (OAuthMember) authentication.getPrincipal();
```

- [ ] **Step 3: Keep the response format aligned with local login**

```java
MemberResDTO.LoginResultDTO.builder()
        .accessToken(accessToken)
        .memberId(member.getMember().getId())
        .email(member.getMember().getEmail())
        .nickname(member.getMember().getNickname())
        .build();
```

### Task 3: Clean Up Kakao OAuth Member Mapping

**Files:**
- Modify: `src/main/java/com/example/umc_study/global/security/service/CustomOAuthService.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/converter/MemberConverter.java`

- [ ] **Step 1: Make Kakao attribute extraction explicit**

```java
Map<String, Object> kakaoAccount = oAuthMember.getAttribute("kakao_account");
Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
String email = kakaoAccount.get("email").toString();
String nickname = profile.get("nickname").toString();
```

- [ ] **Step 2: Upsert member by `socialType + socialUid`**

```java
Member member = memberRepository.findBySocialTypeAndSocialUid(providerId, socialUid)
        .orElseGet(() -> memberRepository.save(MemberConverter.toMember(dto)));
```

- [ ] **Step 3: Return an `OAuthMember` that wraps both `Member` and provider attributes**

```java
return new OAuthMember(member, oAuthMember.getAttributes());
```

### Task 4: Verify the Optional Mission

**Files:**
- Test: `src/test/java/com/example/umc_study/domain/member/controller/MemberControllerTest.java`
- Test: `src/test/java/com/example/umc_study/global/handler/OAuthSuccessHandlerTest.java`

- [ ] **Step 1: Run focused optional OAuth tests**

Run: `.\gradlew.bat test --tests "*MemberControllerTest" --tests "*OAuthSuccessHandlerTest"`
Expected: PASS

- [ ] **Step 2: Run full test suite**

Run: `.\gradlew.bat test`
Expected: PASS

- [ ] **Step 3: Commit**

```bash
git add src/main/java src/test/java docs
git commit -m "feat: add kakao oauth login flow"
```

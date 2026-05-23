# Mandatory JWT Auth Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Finish the workbook's mandatory JWT auth mission with working signup, login, and token-driven my page retrieval.

**Architecture:** Keep the current workbook-aligned identity model where JWT tokens carry `socialUid` and `socialType`, but preserve local login as an `email + password` credential check. Use the JWT filter to rebuild `AuthMember`, then read that principal directly in the my page controller.

**Tech Stack:** Spring Boot, Spring Security, JPA, MockMvc, JJWT

---

### Task 1: Rewrite The Mandatory JWT Tests

**Files:**
- Modify: `src/test/java/com/example/umc_study/domain/member/controller/MemberControllerTest.java`

- [ ] **Step 1: Write a failing my page success test**

Add a test that creates a local member, logs in through `POST /api/login`, extracts the returned token, and calls `GET /api/v2/users/me` with `Authorization: Bearer <token>`.

- [ ] **Step 2: Run the focused member tests and verify they fail**

Run: `.\\gradlew.bat test --tests "*MemberControllerTest"`

Expected: FAIL because the current my page and JWT code are still inconsistent or do not compile.

- [ ] **Step 3: Rewrite the unauthorized my page test**

Replace the old request-body-based unauthorized test with a `GET /api/v2/users/me` request that omits the bearer token and expects `401`.

### Task 2: Repair The JWT Identity Flow

**Files:**
- Modify: `src/main/java/com/example/umc_study/global/security/entity/AuthMember.java`
- Modify: `src/main/java/com/example/umc_study/global/security/util/JwtUtil.java`
- Modify: `src/main/java/com/example/umc_study/global/security/filter/JwtAuthFilter.java`
- Modify: `src/main/java/com/example/umc_study/global/security/service/CustomUserDetailsService.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/repository/MemberRepository.java`

- [ ] **Step 1: Normalize the JWT subject helpers**

Ensure `AuthMember` exposes the real password and returns `socialUid` as the username, then make `JwtUtil` expose matching `getUid(...)` and `getSocialType(...)` helpers.

- [ ] **Step 2: Rebuild principals from JWT claims**

Update `JwtAuthFilter` so it reads `social_type` and subject from the token, loads the member through `CustomUserDetailsService`, and stores the authenticated principal in the security context.

- [ ] **Step 3: Add repository support**

Ensure `MemberRepository` provides `findBySocialTypeAndSocialUid(...)` so both JWT and OAuth groundwork compile.

### Task 3: Finish The Mandatory Member APIs

**Files:**
- Modify: `src/main/java/com/example/umc_study/domain/member/controller/MemberController.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/service/MemberService.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/converter/MemberConverter.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/dto/MemberReqDTO.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/dto/MemberResDTO.java`

- [ ] **Step 1: Finish the token-driven my page API**

Make the controller return `ApiResponse<MyPageResponseDTO>` from `GET /api/v2/users/me` using `@AuthenticationPrincipal AuthMember`.

- [ ] **Step 2: Finish the service logic**

Use the authenticated member from the principal, compute `reviewCount`, and return `MyPageResponseDTO`.

- [ ] **Step 3: Keep local login workbook-compatible**

Validate local members by email and password, then issue the JWT from the workbook-aligned `AuthMember` identity.

### Task 4: Keep OAuth Groundwork Non-Breaking

**Files:**
- Modify: `src/main/java/com/example/umc_study/domain/member/exception/code/MemberErrorCode.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/converter/MemberConverter.java`
- Modify: `src/main/java/com/example/umc_study/global/config/SecurityConfig.java`
- Modify: `src/main/java/com/example/umc_study/global/handler/OAuthSuccessHandler.java`

- [ ] **Step 1: Add the missing OAuth-supporting pieces**

Provide the enum/repository/converter pieces already referenced by the workbook-following OAuth code so the mandatory branch compiles cleanly without removing the groundwork.

- [ ] **Step 2: Fix the handler/config wiring**

Align the `OAuthSuccessHandler` package/imports and response DTO usage with the current codebase.

### Task 5: Verify The Mandatory Mission

**Files:**
- Test: `src/test/java/com/example/umc_study/domain/member/controller/MemberControllerTest.java`

- [ ] **Step 1: Run the focused JWT/member suite**

Run: `.\\gradlew.bat test --tests "*MemberControllerTest"`

Expected: PASS with the new token-driven my page tests.

- [ ] **Step 2: Run the full test suite**

Run: `.\\gradlew.bat test`

Expected: PASS with zero failing tests.

- [ ] **Step 3: Commit the mandatory mission**

Run:

```bash
git add src/main/java src/main/resources src/test/java src/test/resources docs
git commit -m "feat: complete mandatory JWT auth workflow"
```

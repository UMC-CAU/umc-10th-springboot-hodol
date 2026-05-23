# JWT Login API Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a working `POST /api/login` endpoint that returns a JWT access token for valid local members.

**Architecture:** Extend the existing member controller and service so login lives beside signup, while reusing the current security utilities for password checks and JWT generation. Keep security stateless and public-route based by allowing `/api/login` through the current Spring Security configuration.

**Tech Stack:** Spring Boot, Spring Security, JPA, MockMvc, JJWT

---

### Task 1: Document And Stabilize Test Configuration

**Files:**
- Create: `docs/superpowers/specs/2026-05-23-jwt-login-api-design.md`
- Modify: `src/test/resources/application.yml`

- [ ] **Step 1: Ensure the design is captured**

Write the approved behavior into the spec file so the JWT login API shape is explicit before code changes.

- [ ] **Step 2: Add JWT test properties**

Set a deterministic `jwt.token.secretKey` and `jwt.token.expiration.access` in `src/test/resources/application.yml` so Spring can create `JwtUtil` during test boot.

### Task 2: Add Failing Login API Tests

**Files:**
- Modify: `src/test/java/com/example/umc_study/domain/member/controller/MemberControllerTest.java`

- [ ] **Step 1: Write the failing success-path test**

Add a MockMvc test for `POST /api/login` that creates a local member with an encoded password and expects `200` plus a non-empty token payload.

- [ ] **Step 2: Run the focused test and verify it fails**

Run: `.\\gradlew.bat test --tests "*MemberControllerTest"`

Expected: the new login test fails because `/api/login` is not implemented yet, or because the expected response fields do not exist yet.

- [ ] **Step 3: Add credential failure tests**

Add tests that expect `401` for an unknown email and for a wrong password.

### Task 3: Implement The Minimal Login API

**Files:**
- Modify: `src/main/java/com/example/umc_study/domain/member/dto/MemberReqDTO.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/dto/MemberResDTO.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/service/MemberService.java`
- Modify: `src/main/java/com/example/umc_study/domain/member/controller/MemberController.java`
- Modify: `src/main/java/com/example/umc_study/global/config/SecurityConfig.java`

- [ ] **Step 1: Add login request and response DTOs**

Define a validated request record for `email` and `password`, plus a response DTO that contains `accessToken`, `memberId`, `email`, and `nickname`.

- [ ] **Step 2: Add service login logic**

Look up the member by email, verify the password with `PasswordEncoder`, create an `AuthMember`, generate a token with `JwtUtil`, and return the new response DTO. Throw the existing unauthorized error code when credentials are invalid.

- [ ] **Step 3: Add the controller endpoint**

Expose `POST /api/login` under the existing `/api` controller mapping and wrap the result in `ApiResponse.onSuccess(...)`.

- [ ] **Step 4: Allow the route through security**

Add `/api/login` to the public URI list in `SecurityConfig`.

### Task 4: Verify The Behavior

**Files:**
- Test: `src/test/java/com/example/umc_study/domain/member/controller/MemberControllerTest.java`

- [ ] **Step 1: Run the focused member controller tests**

Run: `.\\gradlew.bat test --tests "*MemberControllerTest"`

Expected: the login tests and existing signup tests all pass.

- [ ] **Step 2: Inspect for regressions**

Confirm the output reports zero failures and that the unauthorized/login tests match the intended response codes.

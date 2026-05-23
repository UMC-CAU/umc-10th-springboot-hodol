# Mandatory JWT Auth Design

**Goal:** Complete the workbook's mandatory mission by finishing JWT-based signup, login, and token-driven my page retrieval while preserving the workbook-aligned OAuth groundwork already added to the codebase.

## Scope

- Keep the current JWT + OAuth-oriented security structure in place.
- Make `POST /api/signup` work for local members.
- Make `POST /api/login` validate local email/password credentials and return an access token.
- Make `GET /api/v2/users/me` use the bearer token instead of a request-body member id.
- Keep the my page response in the existing `MyPageResponseDTO` shape.
- Leave OAuth as an unfinished next step, but ensure the current OAuth groundwork does not break compilation.

## Mandatory Flow

1. A local member signs up through `POST /api/signup`.
2. The member logs in through `POST /api/login`.
3. The login response returns an access token.
4. The client sends `Authorization: Bearer <token>` to `GET /api/v2/users/me`.
5. The JWT filter validates the token, rebuilds the authenticated user, and stores it in the security context.
6. The my page controller reads the authenticated principal with `@AuthenticationPrincipal`.

## Data And Identity Rules

- Local members keep `socialType = LOCAL`.
- Local members keep `socialUid = "local:" + email`.
- JWT tokens use the workbook-aligned subject style based on `socialUid`.
- JWT tokens also include `social_type` so the filter can rebuild the authenticated member.
- Local login still authenticates by `email + password`, because that is the workbook's required mandatory flow.

## Response Shape

- Login returns:
  - `accessToken`
  - `memberId`
  - `email`
  - `nickname`
- My page returns the existing profile and activity summary structure:
  - `profile.nickname`
  - `profile.email`
  - `profile.phoneInfo`
  - `activitySummary.currentPointBalance`
  - `activitySummary.reviewCount`

## Testing Strategy

- Replace the old request-body-based my page tests with token-based tests.
- Keep signup and login tests.
- Add a token-driven my page success test that signs in first and then calls `GET /api/v2/users/me`.
- Keep an unauthorized my page test with no bearer token.
- Run the focused member controller suite first, then the full Gradle test suite.

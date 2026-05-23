# JWT Login API Design

**Goal:** Activate a JSON-based login API that authenticates local members with email and password, then returns an access token for JWT-protected endpoints.

## Scope

- Add `POST /api/login` as a public endpoint.
- Accept JSON credentials with `email` and `password`.
- Validate credentials against existing local members.
- Return the existing `ApiResponse` wrapper with a JWT access token and basic member info.
- Keep the existing `GET /login` HTML page as-is.

## Request And Response

- Request body:
  - `email`
  - `password`
- Success response:
  - `accessToken`
  - `memberId`
  - `email`
  - `nickname`
- Failure response:
  - Invalid credentials return the existing unauthorized error shape with HTTP `401`.
  - Invalid request bodies continue using the existing validation error handling.

## Implementation Notes

- Reuse `PasswordEncoder` for password verification.
- Reuse `JwtUtil` for access token creation.
- Reuse `CustomUserDetailsService` and `AuthMember` patterns already present in the project.
- Add `/api/login` to the security allowlist so the endpoint remains public.
- Add JWT properties to the test profile so authentication-related tests can boot reliably.

## Testing

- Login succeeds with correct email and password.
- Login returns `401` for a wrong password.
- Login returns `401` for an unknown email.
- Existing signup behavior remains intact.

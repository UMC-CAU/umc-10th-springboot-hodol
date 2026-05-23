# Optional Kakao OAuth Design

## Goal

워크북의 선택 미션 범위에 맞춰 카카오 OAuth 로그인 후 우리 서버의 JWT를 발급하고, 발급된 JWT로 기존 마이페이지 API를 그대로 사용할 수 있게 만든다.

## Scope

- 카카오 OAuth 시작 URL을 애플리케이션에서 노출한다.
- 카카오 인증 완료 후 사용자 정보를 조회한다.
- `socialType + socialUid` 기준으로 회원을 조회하고, 없으면 신규 생성한다.
- OAuth 성공 시 JSON 응답으로 JWT와 회원 기본 정보를 반환한다.
- 로그인 페이지에서 카카오 로그인 진입 버튼을 제공한다.

## Architecture

- OAuth 시작점은 스프링 시큐리티의 `oauth2Login()` 설정을 그대로 사용한다.
- 사용자 정보 적재는 `CustomOAuthService`가 담당한다.
- OAuth 인증 성공 후 JWT 발급은 `OAuthSuccessHandler`가 담당한다.
- JWT 검증과 마이페이지 인증 흐름은 기존 필수 미션 구현을 그대로 재사용한다.

## Expected Flow

1. 사용자가 `/login` 페이지에서 카카오 로그인 링크를 클릭한다.
2. 브라우저가 `/oauth/authorize/kakao`로 이동하고, 스프링 시큐리티가 카카오 인증 페이지로 리다이렉트한다.
3. 인증 완료 후 카카오가 `/oauth/callback/kakao`로 콜백한다.
4. `CustomOAuthService`가 카카오 사용자 정보를 읽고 회원을 조회 또는 생성한다.
5. `OAuthSuccessHandler`가 해당 회원 기준 JWT를 생성해 JSON으로 응답한다.
6. 클라이언트는 받은 JWT를 `Authorization: Bearer <token>`으로 사용해 마이페이지를 호출한다.

## Data Rules

- 카카오 회원 식별은 `socialType=KAKAO`와 `socialUid=<provider user id>` 조합으로 한다.
- 카카오 회원의 `email`, `nickname`, `socialUid`, `socialType`은 OAuth 응답 기준으로 저장한다.
- 로컬 로그인 회원과 충돌하지 않도록 카카오 회원은 `SocialType.KAKAO`로 분리한다.

## UX

- `/login` 페이지에는 기존 이메일 로그인 안내를 유지한다.
- 같은 페이지에 카카오 로그인 진입 버튼 또는 링크를 추가한다.
- 버튼은 직접 `/oauth/authorize/kakao`로 이동한다.

## Testing

- 로그인 페이지가 카카오 로그인 진입 링크를 렌더링하는지 검증한다.
- OAuth 성공 핸들러가 JWT와 회원 정보를 JSON으로 응답하는지 검증한다.
- 기존 필수 미션 테스트가 깨지지 않는지 전체 테스트로 확인한다.

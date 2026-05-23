package com.example.umc_study.global.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

import static org.assertj.core.api.Assertions.assertThat;

class OAuthFailureHandlerTest {

    @Test
    @DisplayName("oauth failure handler redirects login page with oauth error code")
    void onAuthenticationFailureRedirectsWithOauthReason() throws Exception {
        OAuthFailureHandler handler = new OAuthFailureHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        OAuth2AuthenticationException exception = new OAuth2AuthenticationException(
                new OAuth2Error("authorization_request_not_found"),
                "authorization request not found"
        );

        handler.onAuthenticationFailure(request, response, exception);

        assertThat(response.getRedirectedUrl())
                .isEqualTo("/login?error=social&reason=authorization_request_not_found");
    }
}

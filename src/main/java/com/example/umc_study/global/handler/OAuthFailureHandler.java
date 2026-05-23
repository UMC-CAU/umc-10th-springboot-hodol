package com.example.umc_study.global.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
public class OAuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException, ServletException {
        String reason = extractReason(exception);

        log.error("OAuth login failed with reason: {}", reason, exception);

        String redirectUrl = UriComponentsBuilder.fromPath("/login")
                .queryParam("error", "social")
                .queryParam("reason", reason)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private String extractReason(AuthenticationException exception) {
        if (exception instanceof OAuth2AuthenticationException oauthException) {
            String errorCode = oauthException.getError().getErrorCode();
            if (StringUtils.hasText(errorCode)) {
                return errorCode;
            }
        }

        return exception.getClass().getSimpleName();
    }
}

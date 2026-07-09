package com.example.umc_study.global.security.filter;

import com.example.umc_study.domain.member.enums.SocialType;
import com.example.umc_study.global.security.service.CustomUserDetailsService;
import com.example.umc_study.global.security.util.JwtUtil;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthFilterTest {

    private final JwtUtil jwtUtil = mock(JwtUtil.class);
    private final CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
    private final JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userDetailsService);

    @Test
    @DisplayName("jwt filter does not mask downstream exceptions when authorization header is missing")
    void doesNotMaskDownstreamExceptionsWithoutBearerToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> filter.doFilter(
                request,
                response,
                (req, res) -> {
                    throw new ServletException("oauth callback failed");
                }
        ))
                .isInstanceOf(ServletException.class)
                .hasMessageContaining("oauth callback failed");
    }

    @Test
    @DisplayName("jwt filter returns unauthorized when bearer token processing fails")
    void returnsUnauthorizedWhenBearerTokenProcessingFails() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer broken-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.isValid("broken-token")).thenThrow(new IllegalArgumentException("broken"));

        filter.doFilter(request, response, (req, res) -> {
            throw new AssertionError("filter chain should not continue for broken token");
        });

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("COMMON401_1");
    }

    @Test
    @DisplayName("jwt filter skips oauth callback requests even when authorization header is broken")
    void skipsOauthCallbackRequests() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/oauth/callback/kakao");
        request.setServletPath("/oauth/callback/kakao");
        request.addHeader("Authorization", "Bearer broken-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtUtil.isValid("broken-token")).thenThrow(new IllegalArgumentException("broken"));

        filter.doFilter(request, response, (req, res) -> {
            ((MockHttpServletResponse) res).setStatus(204);
        });

        assertThat(response.getStatus()).isEqualTo(204);
    }
}

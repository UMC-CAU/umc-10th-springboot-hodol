package com.example.umc_study.global.config;

import com.example.umc_study.global.security.exception.CustomAccessDenied;
import com.example.umc_study.global.security.exception.CustomEntryPoint;
import com.example.umc_study.global.security.filter.JwtAuthFilter;
import com.example.umc_study.global.handler.OAuthSuccessHandler;
import com.example.umc_study.global.security.service.CustomUserDetailsService;
import com.example.umc_study.global.security.service.CustomOAuthService;
import com.example.umc_study.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    // 💡 OAuth 관련 필수 의존성 생성자 주입 추가
    private final CustomOAuthService customOAuthService;

    private final String[] allowUris = {
            // Swagger 허용
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/api/signup",
            "/api/login",
            "/login",
            "/logout",
            // 💡 카카오 OAuth 인증 요청 주소들도 시큐리티 필터를 통과할 수 있도록 추가 권장
            "/oauth/authorize/**",
            "/oauth/callback/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // URI 허용 여부
                .authorizeHttpRequests(requests -> requests
                        // Public API 허용
                        .requestMatchers(allowUris).permitAll()
                        // 그 이외 API는 인증 필요
                        .anyRequest().authenticated()
                )
                // 폼 로그인
                .formLogin(AbstractHttpConfigurer::disable)
                // 세션
                .sessionManagement(AbstractHttpConfigurer::disable)
                // JWT 필터
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // ⬇️여기에 OAuth 설정을 추가했습니다!
                // OAuth
                .oauth2Login(oauth -> oauth
                        // 인증 엔트리 포인트
                        .authorizationEndpoint(auth -> auth
                                .baseUri("/oauth/authorize")
                        )
                        // 콜백 주소
                        .redirectionEndpoint(redirect -> redirect
                                .baseUri("/oauth/callback/**")
                        )
                        // 인증 완료 후 정보 활용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuthService)
                        )
                        // 성공 시 JWT 토큰 발행할 핸들러
                        .successHandler(oAuthSuccessHandler())
                )
                // 예외 상황 핸들러 (중복 코드 제거 후 하나로 깔끔하게 정리)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDenied())
                        .authenticationEntryPoint(customEntryPoint())
                );

        return http.build();
    }

    @Bean
    public CustomAccessDenied customAccessDenied(){
        return new CustomAccessDenied();
    }

    @Bean
    public CustomEntryPoint customEntryPoint(){
        return new CustomEntryPoint();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter(){
        return new JwtAuthFilter(jwtUtil, customUserDetailsService);
    }

    // 💡 성공 핸들러를 빈으로 등록하는 메서드 추가
    @Bean
    public OAuthSuccessHandler oAuthSuccessHandler() {
        return new OAuthSuccessHandler(jwtUtil); // 핸들러 생성자 규격에 맞게 인자(jwtUtil 등) 조절 필요
    }
}

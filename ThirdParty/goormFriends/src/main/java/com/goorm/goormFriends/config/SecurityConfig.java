package com.goorm.goormFriends.config;

import com.goorm.goormFriends.common.jwt.JwtAuthenticationEntryPoint;
import com.goorm.goormFriends.common.jwt.TokenProvider;
import com.goorm.goormFriends.common.oauth.PrincipalOauth2UserService;
import com.goorm.goormFriends.handler.JwtAccessDeniedHandler;
import com.goorm.goormFriends.handler.OAuth2AuthenticationFailureHandler;
import com.goorm.goormFriends.handler.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Component
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic(HttpBasicConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers((headersConfigurer) ->
                        headersConfigurer.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                        )
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 시큐리티는 기본적으로 세션 사용
                // 여기서는 세션 -> 쿠키 사용! 세션 설정을 Stateless로 설정
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 로그인, 회원가입 API는 토큰이 없는 상태에서 요청 들어오기 때문에 permitAll 설정
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/user/oauth/login").permitAll()
                                //.requestMatchers(new requestMatchers(PERMIT_URL_ARRAY)).permitAll()
                                .anyRequest().permitAll()
                )

                .apply(new JwtSecurityConfig(tokenProvider))
                .oauth2Login()
                .userInfoEndpoint()
                .userService(principalOauth2UserService)

                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        return http.build();

    }

    private static final String[] PERMIT_URL_ARRAY = {
        ""
    };
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://localhost:8082");
        config.addAllowedMethod("*"); // 모든 메소드 허용.
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}

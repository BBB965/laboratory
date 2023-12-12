package com.goorm.goormFriends.handler;

import com.goorm.goormFriends.common.jwt.TokenProvider;
import com.goorm.goormFriends.common.oauth.PrincipalDetails;
import com.goorm.goormFriends.db.entity.RefreshToken;
import com.goorm.goormFriends.db.entity.User;
import com.goorm.goormFriends.db.repository.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private TokenProvider tokenProvider;
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
        throws IOException, ServletException {

        // redirect 할 url 지정해주기
        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        // 소셜 로그인 성공후 이동할 페이지
        String targetUrl = "/oauth";
        // 추가 정보가 입력되어 있다면 로그인 처리
        if (user.getNickname() != null && user.getProfileImageType() != null) {
            // 토큰 정보 저장하는 페이지로 이동
            targetUrl = "/oauth2";

            // 인증 정보를 기반으로 토큰 생성~~
            String accessToken = tokenProvider.generateAccessToken(authentication);
            String refreshToken = tokenProvider.generateRefreshToken();

            RefreshToken rfToken = RefreshToken.builder()
                    .key(authentication.getName())
                    .value(refreshToken)
                    .build();

            refreshTokenRepository.save(rfToken);

            // 타켓 URL로 토큰 정보를 함께 보내줌
            return UriComponentsBuilder.fromUriString(targetUrl + "?accessToken=" +accessToken)
                    .build().toUriString();
        }

        return UriComponentsBuilder.fromUriString(targetUrl+"?userId" +user.getId())
                .build().toUriString();
    }
}

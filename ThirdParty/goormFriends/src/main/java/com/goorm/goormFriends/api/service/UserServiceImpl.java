package com.goorm.goormFriends.api.service;

import com.goorm.goormFriends.api.dto.LoginResponse;
import com.goorm.goormFriends.common.jwt.TokenProvider;
import com.goorm.goormFriends.db.entity.RefreshToken;
import com.goorm.goormFriends.db.entity.User;
import com.goorm.goormFriends.db.repository.RefreshTokenRepository;
import com.goorm.goormFriends.db.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public String findUserEmailByUserId(int userId) throws Exception {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception("해당 사용자를 찾을 수 없습니다."));
        return user.getEmail();
    }

    @Override
    public String oauthLogin(String email) throws Exception {
        //유저 존재 확인
        if (!userRepository.existsByEmail(email)) {
            throw new Exception("해당 사용자를 찾을 수 없습니다.");
        }
        // MemberId 를 기반으로 refresh token값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(email)
                .orElseThrow(() -> new RuntimeException("이미 로그아웃 된 사용자입니다."));

        // Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken.getValue())) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }
        return refreshToken.getValue();
    }

    @Override
    public String reissue(String accessToken, String refreshToken) throws Exception {
        // refreshToken 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("해당 Refresh Token이 유효하지 않습니다.");
        }
        // access token 에서 member Id 가져오기
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        if (!userRepository.existsByEmail(authentication.getName())) {
            throw new RuntimeException("해당 사용자를 찾을 수 없습니다.");
        }

        // 저장소에서 member Id를 기반으로 refresh token 값 가져오기
        RefreshToken originRefreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // Refresh Toekn 일치 검사
        if (!originRefreshToken.getValue().equals(refreshToken)) {
            throw new RuntimeException("해당 Refresh Token이 일치하지 않습니다.");
        }

        // 새로운 accessToken 생성 + 발급
        return tokenProvider.generateAccessToken(authentication);
    }

    @Override
    public LoginResponse getLoginUser(String email) throws Exception {
        LoginResponse result = null;
        // email을 통해 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("해당 사용자를 찾을 수 없습니다."));
        // 유저를 dto에 감싸기
        result = new LoginResponse(user);
        return result;
    }

    @Override
    public boolean existByEmail(String email) throws Exception {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) throws Exception {
        return userRepository.existByNickName(nickname);
    }
}

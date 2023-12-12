package com.goorm.goormFriends.api.service;

import com.goorm.goormFriends.api.dto.LoginResponse;

public interface UserService {
    String findUserEmailByUserId(int userId) throws Exception;

    String oauthLogin(String email) throws Exception;

    String reissue(String accessToken, String refreshToken) throws Exception;

    LoginResponse getLoginUser(String email) throws Exception;

    boolean existByEmail(String email) throws Exception;

    boolean existsByNickname(String nickname) throws Exception;
}

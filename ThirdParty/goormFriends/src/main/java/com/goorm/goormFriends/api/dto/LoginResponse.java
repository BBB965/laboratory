package com.goorm.goormFriends.api.dto;

import com.goorm.goormFriends.db.entity.User;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class LoginResponse {

    private final int userId;
    private final String nickname;
    private final String profileImageType;
    private final String email;

    public LoginResponse(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.profileImageType = user.getProfileImageType();
        this.email = user.getEmail();
    }
}

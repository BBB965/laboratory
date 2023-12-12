package com.goorm.goormFriends.common.oauth;

public interface OAuth2UserInfo {
    String getProvider();
    String getEmail();
    String getName();
    String getProviderId();
}

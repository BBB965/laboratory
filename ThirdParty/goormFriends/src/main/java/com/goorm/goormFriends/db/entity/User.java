package com.goorm.goormFriends.db.entity;

import com.goorm.goormFriends.common.oauth.OAuth2UserInfo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private int id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = true)
    private String profileImageType;
    @Column(nullable = false)
    private String provider;
    @Column(nullable = false)
    private String providerId;

    public User(OAuth2UserInfo user) {
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
    }
}

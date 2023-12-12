package com.goorm.goormFriends.common.oauth;

import com.goorm.goormFriends.db.entity.User;
import com.goorm.goormFriends.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;

    //userRequest -> code를 받아서 accessToken을 응답받은 객체
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest); // github 회원 프로필 조회

        // code 를 통해 구성한 정보
        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws OAuth2AuthenticationException{

        OAuth2UserInfo oAuth2UserInfo = null;
        if (userRequest.getClientRegistration().getRegistrationId().equals("github")) {
            oAuth2UserInfo = new GithubUserInfo(oAuth2User.getAttributes());
        } else {
            throw new NullPointerException();
        }

        Optional<User> userOptional =
                userRepository.findByProviderAndProviderId(oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId());

        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // 소셜 로그인 유저가 존재하지 않다면
            if (oAuth2UserInfo.getEmail() == null) {
                throw new OAuth2AuthenticationException("이멩일 동의를 하지 않아 회원가입이 불가능합니다.");
            } else if (userRepository.existsByEmail(oAuth2UserInfo.getEmail())) {
                // 이미 해당 이메일로 로컬 회원가입이 되어있는 유저라면
                throw new OAuth2AuthenticationException("이미 가입한 회원입니다.");
            } else {
                // 소셜 로그인 유저는 일반 로그인 불가능~.~
                user = new User(oAuth2UserInfo);
                userRepository.save(user);
            }
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }
}

package com.goorm.goormFriends.db.repository;

import com.goorm.goormFriends.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);
    boolean existByNickName(String nickname);
    Optional<User> findByEmail(String email);
    List<User> findByEmailContainingOrNicknameContaining(String email, String nickname);
    void deleteByEmail(String email);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}

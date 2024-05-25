package com.unity.potato.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    boolean existsByUserEmail (String inputEmail);
    boolean existsByUserNickname (String inputNickname);
    Optional<UserInfo> findByUserEmail(String email);
    Optional<UserInfo> findByUserNickname(String nickname);
}

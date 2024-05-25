package com.unity.potato.domain.notification;

import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.vo.NotificationVo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    long countByUserIdAndIsChecked(Long userId, boolean checked);
    List<Notification> findAllByUserIdAndRegDtAfterOrderByRegDtDesc(Long userId, LocalDateTime oneMonthAgo);
    Optional<Notification> findByUserIdAndLinkAndType(Long id, String link, NotificationType type);
}

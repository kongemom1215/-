package com.unity.potato.service.notification;

import com.unity.potato.domain.notification.Notification;
import com.unity.potato.domain.notification.NotificationRepository;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.vo.NotificationVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public List<Notification> getNotificationList(UserInfo userInfo){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minus(1, ChronoUnit.MONTHS);
        List<Notification> notiList = notificationRepository.findAllByUserIdAndRegDtAfterOrderByRegDtDesc(userInfo.getUserId(), oneMonthAgo);
        return notiList;
    }

    public void checkedNotification(List<Long> alarmIdList, UserInfo userInfo){
        List<Notification> notifications = notificationRepository.findAllById(alarmIdList);
        List<Notification> myNotifications = new ArrayList<>();
        notifications.forEach((notification) -> {
               if(notification.getUserId().equals(userInfo.getUserId())){
                   myNotifications.add(notification);
               }
        });

        myNotifications.stream()
                .forEach(notification -> notification.setChecked(true));

        notificationRepository.saveAll(myNotifications);
    }
}

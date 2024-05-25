package com.unity.potato.domain.vo;

import com.unity.potato.domain.notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class NotificationVo {
    private Long id;
    private NotificationType type;
    private String link;
    private String title;
    private int alarmCnt;
    private boolean checked;
    private LocalDateTime regDt;
}

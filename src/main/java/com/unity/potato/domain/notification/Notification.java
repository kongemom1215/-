package com.unity.potato.domain.notification;

import com.unity.potato.domain.user.UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@Table(name = "notification")
@ToString
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "link")
    private String link;

    @Column(name = "title")
    private String title;

    @Column(name = "alarm_cnt")
    private int alarmCnt;

    @Column(name = "checked")
    private boolean isChecked;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    public void increaseAlarmCnt(){
        this.alarmCnt ++;
    }
    public void decreaseAlarmCnt() { this.alarmCnt --; }

}

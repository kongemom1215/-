package com.unity.potato.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "selected_message")
public class SelectedMessage {
    @Id
    @Column(name = "msg_id")
    private Long msgId;

    @Column(name = "title", nullable = false, length = 15)
    private String title;

    @Column(name = "msg", nullable = false, length = 40)
    private String msg;

    @Column(name = "rgb_color", nullable = false, length = 7)
    private String rgb;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;
}

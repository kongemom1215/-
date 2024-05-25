package com.unity.potato.domain.schedule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "collect_message")
@ToString
public class CollectMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false, length = 15)
    private String title;

    @Column(name = "msg", nullable = false, length = 40)
    private String msg;

    @Column(name = "rgb_color", nullable = false, length = 7)
    private String rgb;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    @Column(name = "selected")
    private Boolean selected = false;
}

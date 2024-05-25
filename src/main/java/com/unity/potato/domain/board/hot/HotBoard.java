package com.unity.potato.domain.board.hot;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class HotBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "hot_date", nullable = false, length = 8)
    private String hotDate;

    @Column(name = "ranking", nullable = false)
    private int ranking;

    @Column(name = "post_link", nullable = false, length = 30)
    private String postLink;

    @Column(name = "post_title", nullable = false, length = 40)
    private String postTitle;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    @Column(name = "hot_degree", nullable = false)
    private int hotDegree;

}

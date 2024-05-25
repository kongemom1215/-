package com.unity.potato.domain.board.worldcup;

import com.unity.potato.config.CachingConfig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class WorldcupInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "img_src", nullable = false, length = 50)
    private String imgSrc;

    @Column(name = "explanation", nullable = false, length = 50)
    private String desc;

    @Column(name = "round", nullable = false)
    private int round;

    @Column(name = "main_visible_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private char visibleYn;
}

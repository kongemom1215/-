package com.unity.potato.domain.board.worldcup;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString
public class WorldcupDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "worldcup_id", nullable = false)
    private Long worldcupId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "img_src", nullable = false, length = 50)
    private String imgSrc;

    @Column(name = "win_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int winCnt;

    public void increaseWinCnt(){
        this.winCnt++;
    }
}

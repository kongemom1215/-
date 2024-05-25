package com.unity.potato.domain.board.free;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
@Getter @Setter
@ToString
public class FreeBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "writer_id")
    private Long writerId;

    @Column(name = "writer_nickname")
    private String writerNickname;

    @Column(name = "title", length = 40, nullable = false)
    private String title;

    @Column(name = "content", length = 2000, nullable = false)
    private String content;

    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    @Column(name = "update_dt")
    private LocalDateTime updateDt;

    @Column(name = "delete_dt")
    private LocalDateTime deleteDt;

    @Column(name = "delete_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private char deleteYn;

    @Column(name = "read_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int readCnt;

    @Column(name = "like_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int likeCnt;

    @Column(name = "reply_cnt", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int replyCnt;

    @Column(name = "img_yn", columnDefinition = "CHAR(1) DEFAULT 'N'")
    private char imgYn;

    public void increaseReadCnt() { this.readCnt++; }
    public void increaseLikeCnt() { this.likeCnt++; }
    public void decreaseLikeCnt() { this.likeCnt--; }
    public void increaseReplyCnt() { this.replyCnt++; }
    public void decreaseReplyCnt() { this.replyCnt--;}

}

package com.unity.potato.domain.vo;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class BoardVo {
    private Long postId;
    private Long writerId;
    private String writerNickname;
    private String title;
    private LocalDateTime regDt;
    private int readCnt;
    private int likeCnt;
    private int replyCnt;
    private char imgYn;
}

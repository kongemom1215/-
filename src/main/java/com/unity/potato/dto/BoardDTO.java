package com.unity.potato.dto;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardDTO {
    private String board_type;
    private String url;
    private String nickname;
    private String title;
    private String content; // 댓글 내용
    private LocalDateTime reg_dt;
    private int read_cnt;
    private int reply_cnt;
    private char img_yn;
}


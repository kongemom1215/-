package com.unity.potato.domain.board;

import java.time.LocalDateTime;

public interface BoardReplyInterface {
    Long getId();
    Long getPostId();
    String getBoardType();
    String getContent();
    LocalDateTime getRegDt();
}

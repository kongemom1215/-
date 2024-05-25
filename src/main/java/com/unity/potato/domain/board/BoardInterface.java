package com.unity.potato.domain.board;

import lombok.Data;

import java.time.LocalDateTime;

public interface BoardInterface {
    String getBoardType();
    Long getId();
    String getRegion();
    String getNickname();
    String getTitle();
    LocalDateTime getRegDt();
    Integer getReadCnt();
    Integer getReplyCnt();
    Character getImgYn();
}

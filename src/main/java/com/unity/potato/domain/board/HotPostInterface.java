package com.unity.potato.domain.board;

public interface HotPostInterface {
    String getBoardType();
    Long getId();
    String getRegion();
    String getTitle();
    int getHotDegree();
}

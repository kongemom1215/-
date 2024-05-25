package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PostRequest {
    @NotBlank
    private String pageCode;
    @NotBlank
    @Size(max=40)
    private String title;
    @NotBlank
    @Size(max = 2000)
    private String content;
    @NotNull
    private char imgYn;

    //맛집게시판 전용 파라미터
    private String region;
    private String hashtag;
    private String imgSrc;
    private KakaoMapVo kakaoMap;
}

package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PostEditRequest {
    @NotBlank
    private String id;
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

    //리뷰 게시판
    private String imgSrc;
    private String hashtag;
    private KakaoMapVo kakaoMap;
}

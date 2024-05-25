package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class PostLikeRequest {
    @NotBlank
    private String postId;
    @NotBlank
    private String pageCode;
}

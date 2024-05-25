package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ReplyListRequest {
    @NotNull
    private Long postId;
    @NotBlank
    private String pageCode;
}

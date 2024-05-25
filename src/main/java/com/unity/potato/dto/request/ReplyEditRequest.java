package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ReplyEditRequest {
    @NotBlank
    private String pageCode;
    @NotBlank
    @Size(max=400)
    private String content;
    @NotNull
    private Long id;
}

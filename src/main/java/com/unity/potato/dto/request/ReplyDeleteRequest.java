package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ReplyDeleteRequest {
    @NotNull
    private Long id;
    @NotBlank
    private String pageCode;
}

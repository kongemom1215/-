package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class MsgCollectRequest {
    @NotBlank
    @Size(max=15)
    private String title;

    @NotBlank
    @Size(max=7)
    private String color;

    @NotBlank
    @Size(max=40)
    private String content;
}

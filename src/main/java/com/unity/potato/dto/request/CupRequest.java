package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CupRequest {
    @NotNull
    private Long cupId;
    @NotNull
    private Long winnerId;
}

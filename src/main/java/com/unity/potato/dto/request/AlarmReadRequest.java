package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
public class AlarmReadRequest {
    @NotEmpty
    List<Long> alarmIdList;
}

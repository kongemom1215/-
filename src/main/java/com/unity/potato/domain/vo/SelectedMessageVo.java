package com.unity.potato.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class SelectedMessageVo {
    private String title;
    private String msg;
    private String rgb;
}

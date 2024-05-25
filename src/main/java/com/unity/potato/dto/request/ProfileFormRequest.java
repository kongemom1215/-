package com.unity.potato.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ProfileFormRequest {
    private String profileImg;
    private String bio;
}

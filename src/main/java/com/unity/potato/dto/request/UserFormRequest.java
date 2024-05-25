package com.unity.potato.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UserFormRequest {
    private String currentPwd;
    private String newPwd;
    private String newPwdRepeat;
    private String nickname;
}

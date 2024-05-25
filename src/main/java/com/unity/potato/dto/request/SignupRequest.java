package com.unity.potato.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String birthDate;
    @NotBlank
    private String nickname;
    @NotBlank
    private String password;
    @NotNull
    private boolean agreeTermsUse;
    @NotNull
    private boolean agreeTermsPrivacy;
    @NotBlank
    private String emailKey;
    @NotBlank
    private String nicknameKey;

    public boolean hasNullField() {
        return email == null ||
                birthDate == null ||
                password == null ||
                emailKey == null ||
                nicknameKey == null;
    }

}

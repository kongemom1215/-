package com.unity.potato.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvUtil {
    @Value("${potato.profile.active}")
    private String activeProfile;

    public boolean isEqualProfile(String input){
        return activeProfile.equals(input);
    }
}

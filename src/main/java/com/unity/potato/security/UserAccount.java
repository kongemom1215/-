package com.unity.potato.security;

import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.util.StringUtil;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {

    private UserInfo userInfo;
    private String profileIcon = null;

    public UserAccount(UserInfo userInfo){
        super(userInfo.getUserNickname(), userInfo.getUserPwd(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.userInfo = userInfo;
        if(!StringUtil.isNullOrEmpty(userInfo.getUserProfile().getUserProfileImg())){
            this.profileIcon = userInfo.getUserProfile().getUserProfileImg();
        }
    }
}

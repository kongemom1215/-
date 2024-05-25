package com.unity.potato.service.validate;

import com.unity.potato.util.RedisUtil;
import com.unity.potato.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.unity.potato.util.constants.ValidateConstants.PREFIX_NICKNAME_VALIDATE;

@Service
public class ValidateService {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 닉네임 중복 체크 여부 redis 적재
     */
    public void saveValidateNickname(String nickname, String uuid){
        redisUtil.add(PREFIX_NICKNAME_VALIDATE+nickname, uuid, 30L);
    }
}

package com.unity.potato.controller.login;

import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.validate.ValidateService;
import com.unity.potato.util.RedisUtil;
import com.unity.potato.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/validate")
public class ValidateController {
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ValidateService validateService;

    /**
     * 닉네임 중복확인
     */
    @GetMapping("/nickname")
    public ResponseEntity<?> validateNickname(@RequestParam String nickname){
        String nicknameKey = StringUtil.generateUuid();

        try {
            if(!StringUtil.isNicknameType(nickname)){
                return ResponseEntity.ok(new Result("9998","닉네임 형식이 맞지 않습니다."));
            }
            if(userInfoRepository.existsByUserNickname(nickname)){
                return ResponseEntity.ok(new Result("9999","중복된 닉네임입니다."));
            }

            validateService.saveValidateNickname(nickname, nicknameKey);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new Result("Error : " + e.getMessage()));
        }


        return ResponseEntity.ok(new Result("0000","사용가능한 닉네임입니다.", nicknameKey));
    }
}

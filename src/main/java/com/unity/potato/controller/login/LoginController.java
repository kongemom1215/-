package com.unity.potato.controller.login;

import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.mail.MailService;
import com.unity.potato.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;

    @PostMapping("/login/find-password")
    public ResponseEntity<?> findPassword(@RequestParam String email) {
        try {
            Result result = mailService.sendFindPwdEmail(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }
}

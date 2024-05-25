package com.unity.potato.controller.login;

import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.dto.request.EmailAuthRequest;
import com.unity.potato.dto.request.SignupRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.mail.MailService;
import com.unity.potato.service.user.SignupService;
import com.unity.potato.util.RedisUtil;
import com.unity.potato.util.StringUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.unity.potato.util.constants.ValidateConstants.PREFIX_DELETE_USER;

@RestController
@RequestMapping("/api/signup")
public class SignupController {

    @Autowired
    private MailService mailService;

    @Autowired
    private SignupService signupService;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 회원가입
     */
    @PostMapping
    public ResponseEntity<?> signupForm(@Valid @RequestBody SignupRequest request, Errors errors){
        try {
            if(errors.hasErrors()){
                return ResponseEntity.ok(new Result("9998", "필수 항목을 기입해주세요."));
            }

            return ResponseEntity.ok(signupService.signup(request));
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "error : " + e.getMessage()));
        }
    }


    /**
        회원가입 이메일 인증 코드 발송
     */
    @PostMapping("/send-email-auth")
    public ResponseEntity<?> sendEmailAuth(@RequestParam("inputEmail") String inputEmail) throws IOException {
        try {
            if(!StringUtil.isEmailType(inputEmail)){
                return ResponseEntity.ok(new Result("9997","이메일 형식으로 입력해주세요."));
            }

            if(mailService.getEmailTryCount(inputEmail) > 10){
                return ResponseEntity.ok(new Result("9998","인증 시도 횟수가 10회 넘었습니다. 내일 다시 시도하시길 바랍니다."));
            }
            Result result = mailService.sendCodeMail(inputEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(new Result("Error : " + e.getMessage()));
        }
    }

    /**
     * 회원가입 인증 메일 코드 확인
     */
    @PostMapping("/email-auth")
    public ResponseEntity<?> emailAuth(@Valid @RequestBody EmailAuthRequest request, Errors errors) {
        try {
            if(errors.hasErrors()){
                return ResponseEntity.ok(new Result("9995", "필수 항목을 기입해주세요."));
            }

            if(mailService.getEmailCodeTryCount(request) > 10){
                return ResponseEntity.ok(new Result("9998","코드 인증 시도 횟수가 10회 넘었습니다. 내일 다시 시도하시길 바랍니다."));
            }

            if(mailService.isCodeVerified(request)){
                if(userInfoRepository.existsByUserEmail(request.getEmail())) {
                    return ResponseEntity.ok(new Result("9997","이미 가입되어있는 이메일입니다."));
                }
                //탈퇴한지 3일 이내인 계정 제한
                if(redisUtil.existData(PREFIX_DELETE_USER+request.getEmail())){
                    return ResponseEntity.ok(new Result("9996","탈퇴한 계정은 탈퇴한 날로부터 3일 뒤에 다시 회원가입을 시도하실 수 있습니다."));
                }

                Map<String, Object> data = mailService.generateUuid(request);
                return ResponseEntity.ok(new Result("0000","ok", data));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999","서버오류"));
        }

        return ResponseEntity.ok(new Result("9999","만료됐거나 올바르지 않은 코드입니다."));
    }


}

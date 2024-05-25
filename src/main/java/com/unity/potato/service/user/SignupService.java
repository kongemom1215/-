package com.unity.potato.service.user;

import com.unity.potato.config.AES128Config;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.domain.user.UserProfile;
import com.unity.potato.domain.user.UserProfileRepository;
import com.unity.potato.dto.request.SignupRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.util.RedisUtil;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.unity.potato.util.constants.EmailConstants.EMAIL_VERIFICATION_ID;
import static com.unity.potato.util.constants.ValidateConstants.PREFIX_NICKNAME_VALIDATE;

@Service
@RequiredArgsConstructor
public class SignupService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private AES128Config aes128Config;

    @Transactional
    public Result signup(SignupRequest request) throws Exception {
        Result result = checkSignupParam(request);

        if(result != null){
            return result;
        }

        return saveUserInfo(request);
    }

    public Result saveUserInfo(SignupRequest request) throws Exception {
        UserInfo userInfo = UserInfo.builder()
                .userEmail(request.getEmail())
                .userBirth(request.getBirthDate())
                .userNickname(request.getNickname())
                .userPwd(passwordEncoder.encode(request.getPassword()))
                .joinDt(LocalDateTime.now())
                .build();

        UserInfo newUserInfo = userInfoRepository.save(userInfo);
        UserProfile userProfile = new UserProfile(userInfo);
        userProfileRepository.save(userProfile);

        if (newUserInfo != null && newUserInfo.getUserId() != null) {
            // 저장에 성공한 경우
            redisUtil.delete(EMAIL_VERIFICATION_ID+request.getEmail());
            redisUtil.delete(PREFIX_NICKNAME_VALIDATE+request.getNickname());
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("enUserId", aes128Config.encryptAes(String.valueOf(newUserInfo.getUserId())));
            return new Result("0000", "회원가입을 성공하였습니다.", resultData);
        } else {
            return new Result("9930", "회원가입이 정상적으로 진행되지 않았습니다. 계속될 경우 문의 부탁드립니다.");
        }
    }

    public Result checkSignupParam(SignupRequest request){
        if(!request.isAgreeTermsUse() || !request.isAgreeTermsPrivacy()){
            return new Result("9990", "필수 약관에 동의해주세요.");
        }

        String redisEmailKey = redisUtil.getValue(EMAIL_VERIFICATION_ID+request.getEmail());
        String redisNicknameKey = redisUtil.getValue(PREFIX_NICKNAME_VALIDATE+request.getNickname());

        if(StringUtil.isNullOrEmpty(redisEmailKey) || !redisEmailKey.equals(request.getEmailKey())){
            return new Result("9980", "해당 이메일 인증 여부가 누락되었습니다. 회원가입을 다시 진행해주세요.");
        }
        if(StringUtil.isNullOrEmpty(redisNicknameKey) || !redisNicknameKey.equals(request.getNicknameKey())){
            return new Result("9970", "닉네임 중복여부를 확인해주세요.");
        }

        //DB 메일 체크
        if(userInfoRepository.existsByUserEmail(request.getEmail())){
            redisUtil.delete(EMAIL_VERIFICATION_ID+request.getEmail());
            return new Result("9960","죄송합니다. 입력하신 이메일은 이미 다른 사용자가 사용 중입니다. 회원가입을 다시 진행해주세요.");
        }

        //DB 이메일 체크
        if(userInfoRepository.existsByUserNickname(request.getNickname())){
            redisUtil.delete(PREFIX_NICKNAME_VALIDATE+request.getNickname());
            return new Result("9961","죄송합니다. 선택하신 닉네임은 이미 다른 사용자가 사용 중입니다. 다른 닉네임을 선택해 주세요.");
        }

        if(!StringUtil.isValidDateAndAge(request.getBirthDate())) {
            return new Result("9950", "형식에 맞지않는 생년월일입니다.");
        }

        if(!StringUtil.isValidPassword(request.getPassword(), request.getEmail())){
            return new Result("9951", "형식에 맞지않는 비밀번호입니다.");
        }

        return null;
    }
}

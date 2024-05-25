package com.unity.potato.service.user;

import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.dto.request.ProfileFormRequest;
import com.unity.potato.dto.request.UserFormRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.util.RedisUtil;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.unity.potato.util.constants.ValidateConstants.PREFIX_CHANGE_PASSWORD;

@Service
@RequiredArgsConstructor
public class ProfileService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisUtil redisUtil;

    @Transactional
    public void updateUserProfile(ProfileFormRequest request, UserInfo userInfo){
        if(!StringUtil.isNullOrEmpty(request.getBio())){
            userInfo.getUserProfile().setBio(request.getBio());
        }

        if(!StringUtil.isNullOrEmpty(request.getProfileImg())){
            userInfo.getUserProfile().setUserProfileImg(request.getProfileImg());
        }

        userInfoRepository.save(userInfo);
    }

    @Transactional
    public Result updateUserPassword(UserInfo userInfo, UserFormRequest request){
        // 현재 비밀번호가 맞는지 체크
        if(userInfo == null || !passwordEncoder.matches(request.getCurrentPwd(), userInfo.getUserPwd())) {
            return new Result("9000", "비밀번호가 일치하지 않습니다.");
        }

        String newPassword = request.getNewPwd();

        // 비밀번호 형식 체크
        if(!StringUtil.isValidPassword(newPassword, userInfo.getUserEmail())){
            return new Result("9951", "형식에 맞지않는 비밀번호입니다.");
        }
        // 입력한 새 비밀번호와 재입력한 새 비밀번호가 일치하는지 체크
        if(!newPassword.equals(request.getNewPwdRepeat())){
            return new Result("9000", "재입력한 비밀번호가 일치하지 않습니다.");
        }
        // 기존비밀번호와 동일한지 체크
        if(passwordEncoder.matches(newPassword, userInfo.getUserPwd())){
            return new Result("9961", "기존과 동일한 비밀번호로 재설정할 수 없습니다.");
        }

        //userPwd(passwordEncoder.encode(request.getPassword()
        userInfo.setUserPwd(passwordEncoder.encode(newPassword));
        userInfoRepository.save(userInfo);

        if(redisUtil.existData(PREFIX_CHANGE_PASSWORD+userInfo.getUserEmail())){
            redisUtil.delete(PREFIX_CHANGE_PASSWORD+userInfo.getUserEmail());
        }

        return new Result("0000", "비밀번호 변경 성공");
    }

    @Transactional
    public Result updateUserNickname(UserInfo userInfo, UserFormRequest request){
        if(!StringUtil.isNicknameType(request.getNickname())){
            return new Result("9998","닉네임 형식이 맞지 않습니다.");
        }

        if(userInfoRepository.existsByUserNickname(request.getNickname())){
            return new Result("9997","중복된 닉네임입니다.");
        }

        userInfo.setUserNickname(request.getNickname());
        userInfoRepository.save(userInfo);

        return new Result("0000", "닉네임 변경 성공");
    }

}

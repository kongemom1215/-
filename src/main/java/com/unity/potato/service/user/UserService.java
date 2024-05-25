package com.unity.potato.service.user;

import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.domain.user.UserProfile;
import com.unity.potato.dto.response.Result;
import com.unity.potato.security.UserAccount;
import com.unity.potato.util.LoginUtil;
import com.unity.potato.util.RedisUtil;
import com.unity.potato.util.constants.ValidateConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.unity.potato.util.constants.ValidateConstants.PREFIX_DELETE_USER;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private RedisUtil redisUtil;

    public void updateUser(UserInfo userInfo, HttpServletRequest request){
        HttpSession session = request.getSession();
        UserDetails userDetail = loadUserByUsername(userInfo.getUserEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        UserInfo userinfo =  userInfoRepository.findByUserEmail(emailOrNickname).orElse(null);
        if(userinfo == null){
            userinfo = userInfoRepository.findByUserNickname(emailOrNickname).orElse(null);
        }
        if(userinfo == null){
            throw new UsernameNotFoundException(emailOrNickname);
        }
        return new UserAccount(userinfo);
    }

    @Transactional
    public Result deleteUser(UserInfo userInfo){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String deleteTime = dateTime.format(formatter);
        redisUtil.addWithExpireDay(PREFIX_DELETE_USER+userInfo.getUserEmail(), deleteTime, 3);
        userInfoRepository.delete(userInfo);

        return new Result("0000", "정상적으로 탈퇴 처리되었습니다.");
    }

    public String getWriterProfileImg(Long writerId){
        UserInfo userInfo = userInfoRepository.findById(writerId).orElse(null);
        if(userInfo != null){
            UserProfile userProfile = userInfo.getUserProfile();
            if(userProfile != null){
                return userProfile.getUserProfileImg();
            }
        }
        return null;
    }

}

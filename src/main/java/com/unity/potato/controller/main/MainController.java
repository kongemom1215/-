package com.unity.potato.controller.main;

import com.unity.potato.config.AES128Config;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.security.CurrentUser;
import com.unity.potato.service.board.hot.HotBoardService;
import com.unity.potato.service.collect.CollectService;
import com.unity.potato.util.RedisUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.unity.potato.util.constants.EmailConstants.EMAIL_VERIFICATION_ID;
import static com.unity.potato.util.constants.ValidateConstants.PREFIX_CHANGE_PASSWORD;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AES128Config aes128Config;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private CollectService collectService;
    @Autowired
    private HotBoardService hotBoardService;

    @RequestMapping("/community/main")
    public String goMainPage(@CurrentUser UserInfo userInfo, HttpServletResponse response, Model model){

        try{
            if(userInfo != null){
                model.addAttribute(userInfo);
                if(redisUtil.existData(PREFIX_CHANGE_PASSWORD+userInfo.getUserEmail())){
                    model.addAttribute("isChangePwd", true);
                }
            }
            model.addAttribute("msgVo", collectService.getSelectedMsg());
            model.addAttribute("hotPosts", hotBoardService.getMainHotPosts());
        } catch (Exception e) {
            return "error/400";
        }

        return "communityMain";
    }

    @RequestMapping("/community/login")
    public String goLoginPage(String errorCode, Model model){
        model.addAttribute("errorCode", errorCode);
        return "login";
    }

    @RequestMapping("/community/login/findPwd")
    public String goFindPasswordPage(){
        return "findPassword";
    }

    @RequestMapping("/community/login/signup")
    public String goSignupPage(){
        return "signup";
    }


    @RequestMapping("/community/login/signupForm")
    public String goSignupForm(@RequestParam String uuid, @RequestParam String email){
        if(uuid.equals(redisUtil.getValue(EMAIL_VERIFICATION_ID+email))){
            return "signupForm";
        }
        return "error/400";
    }

    @RequestMapping("/community/login/signupSuccess")
    public String goSignupCompletePage(@RequestParam String enUserId, Model model){
        try {
            String decUserId = aes128Config.decryptAes(enUserId);
            UserInfo userInfo = userInfoRepository.findById(Long.valueOf(decUserId)).orElse(null);
            if(userInfo == null){
                model.addAttribute("error", "access fail");
                return "signupComplete";
            }
            model.addAttribute("nickname", userInfo.getUserNickname());
            model.addAttribute("numberOfUser", userInfo.getUserId());

        } catch (Exception e){
            model.addAttribute("error", "access fail");
        }
        return "signupComplete";
    }

    @RequestMapping("/term/agreement")
    public String goTermOfUsePage(){
        return "term/termOfUse";
    }

    @RequestMapping("/term/privacy")
    public String goTermOfPrivacy(){
        return "term/termOfPrivacy";
    }
}

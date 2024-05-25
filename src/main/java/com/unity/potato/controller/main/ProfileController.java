package com.unity.potato.controller.main;

import com.unity.potato.domain.board.BoardInterface;
import com.unity.potato.domain.board.BoardLikeInterface;
import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardLikeRepository;
import com.unity.potato.domain.board.free.FreeBoardReplyRepository;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.board.recipe.RecipeBoardLikeRepository;
import com.unity.potato.domain.board.recipe.RecipeBoardReplyRepository;
import com.unity.potato.domain.board.recipe.RecipeBoardRepository;
import com.unity.potato.domain.board.share.ShareBoard;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.domain.user.UserProfile;
import com.unity.potato.dto.BoardDTO;
import com.unity.potato.dto.BoardPageDTO;
import com.unity.potato.dto.request.ProfileFormRequest;
import com.unity.potato.dto.request.UserFormRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.security.CurrentUser;
import com.unity.potato.service.board.BoardService;
import com.unity.potato.service.board.free.FreeBoardService;
import com.unity.potato.service.board.share.ShareBoardService;
import com.unity.potato.service.user.ProfileService;
import com.unity.potato.service.user.UserService;
import com.unity.potato.util.LoginUtil;
import com.unity.potato.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    @Autowired
    private ProfileService profileService;
    @Autowired
    private UserService userService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private LoginUtil loginUtil;

    @GetMapping("/profile/{userId}")
    public String viewProfile(@PathVariable String userId, Model model, @CurrentUser UserInfo userInfo){
        if(StringUtil.isNullOrEmpty(userId) || !StringUtil.isNumberic(userId)){
            return "error/404";
        }
        UserInfo profileUser = userInfoRepository.findById(Long.valueOf(userId)).orElse(null);
        if(profileUser == null){
            model.addAttribute("error", "탈퇴한 회원이거나 존재하지 않는 회원입니다.");
            return "profile";
        }

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("joinDtStr", StringUtil.LocalDateTimeToString(profileUser.getJoinDt()));
        model.addAttribute("writeCnt", boardService.getWriteCount(profileUser.getUserId()));
        model.addAttribute("replyCnt", boardService.getReplyCount(profileUser.getUserId()));
        UserProfile userProfile = profileUser.getUserProfile();
        if(userProfile != null){
            model.addAttribute(userProfile);
        }

        if(userInfo!= null){
            boolean isOwner = profileUser.getUserId().equals(userInfo.getUserId());
            model.addAttribute("isOwner", isOwner);
        }

        return "profile";
    }


    @GetMapping("/profile/setting")
    public String profileUpdateForm(@CurrentUser UserInfo userInfo, Model model) {
        if(userInfo == null){
            return "error/404";
        }
        model.addAttribute(userInfo);
        model.addAttribute("userProfile", userInfo.getUserProfile());
        model.addAttribute("isOwner", true);

        return "profileForm";
    }

    @PostMapping("/api/profile/update-setting")
    public ResponseEntity<?> profileUpdate(@CurrentUser UserInfo userInfo, @RequestBody ProfileFormRequest request, HttpServletRequest httpRequest){
        try {
            profileService.updateUserProfile(request, userInfo);
            userService.updateUser(userInfo, httpRequest);
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }

        return ResponseEntity.ok(new Result("0000", "ok", userInfo.getUserId()));
    }

    @PostMapping("/api/profile/update-password")
    public ResponseEntity<?> updatePassword(@CurrentUser UserInfo userInfo, @RequestBody UserFormRequest request, HttpServletRequest httpRequest){
        try {
            if(userInfo != null){
                Result result = profileService.updateUserPassword(userInfo, request);
                userService.updateUser(userInfo, httpRequest);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }

        return ResponseEntity.ok(new Result("9999", "요청 값이 누락되었습니다"));
    }

    @PostMapping("/api/profile/update-nickname")
    public ResponseEntity<?> updateNickname(@CurrentUser UserInfo userInfo, @RequestBody UserFormRequest request, HttpServletRequest httpRequest){
        try {
            if(userInfo != null){
                Result result = profileService.updateUserNickname(userInfo, request);
                userService.updateUser(userInfo, httpRequest);
                return ResponseEntity.ok(result);
            }
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }

        return ResponseEntity.ok(new Result("9999", "요청 값이 누락되었습니다"));
    }

    @PostMapping("/api/profile/withdraw")
    public ResponseEntity<?> withdrawUser(@CurrentUser UserInfo userInfo, @RequestParam boolean isAgree, HttpServletRequest request, HttpServletResponse response){
        try {
            if(isAgree && userInfo != null){
                Result result = userService.deleteUser(userInfo);
                loginUtil.processLogout(request,response);
                return ResponseEntity.ok(result);
            }
        }catch (Exception e){
        }
        return ResponseEntity.ok(new Result("9999", "정상 처리되지 않았습니다."));
    }

    @PostMapping("/api/profile/post-list")
    public ResponseEntity<?> getPostList(@RequestParam String category, @RequestParam String userId, @RequestParam(required = false, defaultValue = "0", value = "page") String page){
        if(!StringUtil.isNumberic(page) | !StringUtil.isNumberic(userId)){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청 값입니다."));
        }

        try {
            UserInfo profileUser = userInfoRepository.findById(Long.valueOf(userId)).orElse(null);
            if(profileUser != null){
                BoardPageDTO boardList = boardService.getBoardList(category, Integer.parseInt(page), profileUser.getUserId());
                return ResponseEntity.ok(new Result("0000", "조회 성공", boardList));
            } else {
                return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청입니다."));
            }
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "불러오는 도중 오류가 발생하였습니다."));
        }
    }
}

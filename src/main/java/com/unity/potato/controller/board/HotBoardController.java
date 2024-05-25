package com.unity.potato.controller.board;

import com.unity.potato.domain.board.hot.HotBoard;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.dto.response.Result;
import com.unity.potato.security.CurrentUser;
import com.unity.potato.service.board.hot.HotBoardService;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HotBoardController {
    @Autowired
    private HotBoardService hotBoardService;

    @RequestMapping("/community/hot")
    public String goHotBoard(
            @CurrentUser UserInfo userInfo,
            Model model){
        try {
            model.addAttribute("dateList", hotBoardService.getDate());
            model.addAttribute("boardList", hotBoardService.getBoardList());
        } catch (Exception e){
            model.addAttribute("error", "처리 도중 에러가 발생하였습니다.");
        }

        return "hotBoard";
    }

}

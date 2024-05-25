package com.unity.potato.controller.board;

import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.security.CurrentUser;
import com.unity.potato.service.board.free.FreeBoardService;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class FreeBoardController {

    @Autowired
    private FreeBoardService freeBoardService;

    @RequestMapping("/community/free")
    public String goFreeBoard(
            @RequestParam(required = false, defaultValue = "0", value = "page") String page,
            @RequestParam(required = false) String sort,
            @CurrentUser UserInfo userInfo,
            Model model){
        try {
            if(StringUtil.isNumberic(page)){
                model.addAttribute("boardList", freeBoardService.getBoardList(page, sort));
                if(!StringUtil.isNullOrEmpty(sort)){
                    model.addAttribute("sort", sort);
                }
            } else {
                model.addAttribute("error", "유효하지 않은 페이지 요청입니다.");
            }
        } catch (Exception e){
            model.addAttribute("error", "처리 도중 에러가 발생하였습니다.");
        }

        return "freeBoard";
    }

    @RequestMapping("/community/free/{id}")
    public String goFreeBoardDetail(@PathVariable String id, Model model, @CurrentUser UserInfo userInfo){
        if(!StringUtil.isNumberic(id)){
            model.addAttribute("error", "유효하지 않은 URL입니다.");
            return "freeBoardDetail";
        }

        try {
            freeBoardService.getBoardInfo(id, userInfo, model);
        } catch (Exception e){
            model.addAttribute("error", "유효하지 않은 URL입니다.");
        }

        return "freeBoardDetail";
    }

    @RequestMapping("/community/free/edit/{id}")
    public String goEditFreeBoard(@PathVariable String id, @CurrentUser UserInfo userInfo, Model model){
        
        if(!StringUtil.isNumberic(id)){
            model.addAttribute("error", "유효하지 않은 ID입니다");
            return "freeBoardEdit";
        }

        try {
            freeBoardService.getBoardEditInfo(id, userInfo, model);
        } catch (Exception e){
            model.addAttribute("error", "유효하지 않은 접근입니다.");
        }

        return "freeBoardEdit";
    }

    @RequestMapping("/community/free/write")
    public String goWriteFreeBoard(@CurrentUser UserInfo userInfo, Model model){
                return "freeBoardWrite";
    }

}

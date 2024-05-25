package com.unity.potato.controller.board;

import com.unity.potato.service.board.search.SearchService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/community/search")
    public String searchAll(@RequestParam String keyword, Model model){
        if(StringUtil.isNullOrEmpty(keyword)){
            return "error/404";
        }

        try {
            model.addAttribute("keyword", keyword);
            model.addAttribute("freeBoardResult", searchService.searchEntireFreeBoard(keyword));
            model.addAttribute("shareBoardResult", searchService.searchEntireShareBoard(keyword));
            model.addAttribute("reviewBoardResult", searchService.searchEntireReviewBoard(keyword));
            model.addAttribute("recipeBoardResult", searchService.searchEntireRecipeBoard(keyword));
        } catch (Exception e){
            return "error/500";
        }

        return "entireSearch";
    }

    @RequestMapping("/community/{board}/search")
    public String searchBoard(  @PathVariable String board,
                                @RequestParam String type, @RequestParam String keyword, @RequestParam(required = false) String page, Model model,
                                @PageableDefault(size = 10, sort = "regDt", direction = Sort.Direction.DESC) Pageable pageable){
        if(StringUtil.isNullOrEmpty(keyword) || StringUtil.isNullOrEmpty(type) || StringUtil.isNullOrEmpty(board)){
            return "error/404";
        }
        if(!StringUtil.isNullOrEmpty(page) && StringUtil.isNumberic(page)){
            pageable = PageRequest.of(Integer.parseInt(page), 10, Sort.by(Sort.Direction.DESC, "regDt"));
        }

        try {
            model.addAttribute("boardPage", searchService.searchBoard(board, type, keyword, pageable));
            model.addAttribute("searchText", keyword);
            model.addAttribute("searchType", type);
        } catch (Exception e){
            return "error/500";
        }

        if("free".equals(board) || "share".equals(board) || "recipe".equals(board) || "review".equals(board)){
            return board + "Search";
        } else {
            return "error/404";
        }
    }
}

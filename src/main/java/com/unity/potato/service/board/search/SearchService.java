package com.unity.potato.service.board.search;

import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.board.recipe.RecipeBoard;
import com.unity.potato.domain.board.recipe.RecipeBoardRepository;
import com.unity.potato.domain.board.review.ReviewBoard;
import com.unity.potato.domain.board.review.ReviewBoardRepository;
import com.unity.potato.domain.board.share.ShareBoard;
import com.unity.potato.domain.board.share.ShareBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    @Autowired
    private FreeBoardRepository freeBoardRepository;
    @Autowired
    private ShareBoardRepository shareBoardRepository;
    @Autowired
    private ReviewBoardRepository reviewBoardRepository;
    @Autowired
    private RecipeBoardRepository recipeBoardRepository;

    @Transactional
    public Page<?> searchBoard(String board, String type, String keyword, Pageable pageable){
        if("free".equals(board)){
            return freeBoardSearch(type, keyword, pageable);
        } else if("share".equals(board)){
            return shareBoardSearch(type, keyword, pageable);
        } else if("recipe".equals(board)){
            return recipeBoardSearch(type, keyword, pageable);
        } else if("review".equals(board)){
            return reviewBoardSearch(type, keyword, pageable);
        } else {
            return null;
        }
    }

    public Page<FreeBoard> freeBoardSearch(String type, String keyword, Pageable pageable){
        if("title".equals(type)){
            return freeBoardRepository.searchByTitle(keyword, pageable);
        } else if("content".equals(type)){
            return freeBoardRepository.searchByContent(keyword, pageable);
        } else {
            return freeBoardRepository.searchByTitleAndContent(keyword, pageable);
        }
    }
    public Page<ShareBoard> shareBoardSearch(String type, String keyword, Pageable pageable){
        if("title".equals(type)){
            return shareBoardRepository.searchByTitle(keyword, pageable);
        } else if("content".equals(type)){
            return shareBoardRepository.searchByContent(keyword, pageable);
        } else {
            return shareBoardRepository.searchByTitleAndContent(keyword, pageable);
        }
    }
    public Page<RecipeBoard> recipeBoardSearch(String type, String keyword, Pageable pageable){
        if("title".equals(type)){
            return recipeBoardRepository.searchByTitle(keyword, pageable);
        } else if("content".equals(type)){
            return recipeBoardRepository.searchByContent(keyword, pageable);
        } else {
            return recipeBoardRepository.searchByTitleAndContent(keyword, pageable);
        }
    }
    public Page<ReviewBoard> reviewBoardSearch(String type, String keyword, Pageable pageable){
        if("title".equals(type)){
            return reviewBoardRepository.searchByTitle(keyword, pageable);
        } else if("content".equals(type)){
            return reviewBoardRepository.searchByContent(keyword, pageable);
        } else {
            return reviewBoardRepository.searchByTitleAndContent(keyword, pageable);
        }
    }

    @Transactional
    public Map<String, ?> searchEntireFreeBoard(String keyword){
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("post_cnt", freeBoardRepository.countSearch(keyword));
        searchMap.put("post_list", freeBoardRepository.searchBoardLimit3(keyword));
        return searchMap;
    }

    @Transactional
    public Map<String, ?> searchEntireShareBoard(String keyword){
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("post_cnt", shareBoardRepository.countSearch(keyword));
        searchMap.put("post_list", shareBoardRepository.searchBoardLimit3(keyword));
        return searchMap;
    }

    @Transactional
    public Map<String, ?> searchEntireReviewBoard(String keyword){
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("post_cnt", reviewBoardRepository.countSearch(keyword));
        searchMap.put("post_list", reviewBoardRepository.searchBoardLimit3(keyword));
        return searchMap;
    }

    @Transactional
    public Map<String, ?> searchEntireRecipeBoard(String keyword){
        Map<String, Object> searchMap = new HashMap<>();
        searchMap.put("post_cnt", recipeBoardRepository.countSearch(keyword));
        searchMap.put("post_list", recipeBoardRepository.searchBoardLimit3(keyword));
        return searchMap;
    }
}

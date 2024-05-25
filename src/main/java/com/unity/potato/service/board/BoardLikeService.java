package com.unity.potato.service.board;

import com.unity.potato.domain.board.BoardLikeInterface;
import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardLike;
import com.unity.potato.domain.board.free.FreeBoardLikeRepository;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.board.share.ShareBoard;
import com.unity.potato.domain.board.share.ShareBoardLike;
import com.unity.potato.domain.board.share.ShareBoardLikeRepository;
import com.unity.potato.domain.board.share.ShareBoardRepository;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.dto.request.PostLikeRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.board.free.FreeBoardLikeService;
import com.unity.potato.service.board.recipe.RecipeBoardLikeService;
import com.unity.potato.service.board.review.ReviewBoardLikeService;
import com.unity.potato.service.board.share.ShareBoardLikeService;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardLikeService {

    @Autowired
    private FreeBoardLikeService freeBoardLikeService;
    @Autowired
    private ShareBoardLikeService shareBoardLikeService;
    @Autowired
    private ReviewBoardLikeService reviewBoardLikeService;
    @Autowired
    private RecipeBoardLikeService recipeBoardLikeService;



    @Transactional
    public Result updateBoardLike(PostLikeRequest request, UserInfo userInfo){
        if(!StringUtil.isNumberic(request.getPostId())){
            return new Result("9999", "올바르지 않은 요청값입니다.");
        }
        if("free".equals(request.getPageCode())){
            freeBoardLikeService.updateBoardLike(request, userInfo);
        } else if("share".equals(request.getPageCode())){
            shareBoardLikeService.updateBoardLike(request, userInfo);
        } else if("review".equals(request.getPageCode())){
            reviewBoardLikeService.updateBoardLike(request, userInfo);
        } else if("recipe".equals(request.getPageCode())){
            recipeBoardLikeService.updateBoardLike(request, userInfo);
        }

        return new Result("0000", "정상 처리하였습니다.");
    }

    @Transactional
    public Result updateBoardUnlike(PostLikeRequest request, UserInfo userInfo){
        if(!StringUtil.isNumberic(request.getPostId())){
            return new Result("9999", "올바르지 않은 요청값입니다.");
        }
        if("free".equals(request.getPageCode())){
            freeBoardLikeService.updateBoardUnLike(request, userInfo);
        } else if("share".equals(request.getPageCode())){
            shareBoardLikeService.updateBoardUnLike(request, userInfo);
        } else if("review".equals(request.getPageCode())){
            reviewBoardLikeService.updateBoardUnLike(request, userInfo);
        } else if("recipe".equals(request.getPageCode())){
            recipeBoardLikeService.updateBoardUnLike(request, userInfo);
        }

        return new Result("0000", "정상 처리하였습니다.");
    }
}

package com.unity.potato.service.board;

import com.unity.potato.domain.board.free.FreeBoardReply;
import com.unity.potato.domain.board.free.FreeBoardReplyRepository;
import com.unity.potato.domain.board.recipe.RecipeBoardReply;
import com.unity.potato.domain.board.recipe.RecipeBoardReplyRepository;
import com.unity.potato.domain.board.review.ReviewBoardReply;
import com.unity.potato.domain.board.review.ReviewBoardReplyRepository;
import com.unity.potato.domain.board.share.ShareBoardReply;
import com.unity.potato.domain.board.share.ShareBoardReplyRepository;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.dto.request.ReplyDeleteRequest;
import com.unity.potato.dto.request.ReplyEditRequest;
import com.unity.potato.dto.request.ReplyListRequest;
import com.unity.potato.dto.request.ReplyRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.board.free.FreeBoardReplyService;
import com.unity.potato.service.board.free.FreeBoardService;
import com.unity.potato.service.board.recipe.RecipeBoardReplyService;
import com.unity.potato.service.board.review.ReviewBoardReplyService;
import com.unity.potato.service.board.share.ShareBoardReplyService;
import com.unity.potato.service.board.share.ShareBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardReplyService {

    @Autowired
    private FreeBoardReplyRepository freeBoardReplyRepository;
    @Autowired
    private ShareBoardReplyRepository shareBoardReplyRepository;
    @Autowired
    private ReviewBoardReplyRepository reviewBoardReplyRepository;
    @Autowired
    private RecipeBoardReplyRepository recipeBoardReplyRepository;
    @Autowired
    private FreeBoardReplyService freeBoardReplyService;
    @Autowired
    private ShareBoardReplyService shareBoardReplyService;
    @Autowired
    private ReviewBoardReplyService reviewBoardReplyService;
    @Autowired
    private RecipeBoardReplyService recipeBoardReplyService;

    @Transactional
    public Result uploadReply(ReplyRequest request, UserInfo userInfo){
        if("free".equals(request.getPageCode())){
            return freeBoardReplyService.uploadReply(request, userInfo);
        } else if("share".equals(request.getPageCode())){
            return shareBoardReplyService.uploadReply(request, userInfo);
        } else if("review".equals(request.getPageCode())){
            return reviewBoardReplyService.uploadReply(request, userInfo);
        } else if("recipe".equals(request.getPageCode())){
            return recipeBoardReplyService.uploadReply(request, userInfo);
        }

        return new Result("9998", "올바르지 않은 요청 값입니다.");
    }

   @Transactional
    public List<Map<String, Object>> getReplyList(ReplyListRequest request, UserInfo userInfo){
        if("free".equals(request.getPageCode())){
            List<FreeBoardReply> replyList = freeBoardReplyRepository.findAllByPostIdAndDeleteDtNull(request.getPostId());
            if(!replyList.isEmpty()){
                return freeBoardReplyService.formmatedReplyList(replyList, userInfo);
            }
        } else if("share".equals(request.getPageCode())){
            List<ShareBoardReply> replyList = shareBoardReplyRepository.findAllByPostIdAndDeleteDtNull(request.getPostId());
            if(!replyList.isEmpty()){
                return shareBoardReplyService.formmatedReplyList(replyList, userInfo);
            }
        } else if("review".equals(request.getPageCode())){
            List<ReviewBoardReply> replyList = reviewBoardReplyRepository.findAllByPostIdAndDeleteDtNull(request.getPostId());
            if(!replyList.isEmpty()){
                return reviewBoardReplyService.formmatedReplyList(replyList, userInfo);
            }
        } else if("recipe".equals(request.getPageCode())){
            List<RecipeBoardReply> replyList = recipeBoardReplyRepository.findAllByPostIdAndDeleteDtNull(request.getPostId());
            if(!replyList.isEmpty()){
                return recipeBoardReplyService.formmatedReplyList(replyList, userInfo);
            }
        }
        return null;
    }

    @Transactional
    public Result deleteReply(ReplyDeleteRequest request, Long userId) {
        if("free".equals(request.getPageCode())){
            return freeBoardReplyService.deleteReply(request.getId(), userId);
        } else if("share".equals(request.getPageCode())){
            return shareBoardReplyService.deleteReply(request.getId(), userId);
        } else if("review".equals(request.getPageCode())){
            return reviewBoardReplyService.deleteReply(request.getId(), userId);
        } else if("recipe".equals(request.getPageCode())){
            return recipeBoardReplyService.deleteReply(request.getId(), userId);
        }

        return new Result("9998", "올바르지 않은 요청 값입니다.");
    }

    @Transactional
    public Result editReply(ReplyEditRequest request, UserInfo userInfo){
        if("free".equals(request.getPageCode())){
            return freeBoardReplyService.editReply(request, userInfo);
        } else if("share".equals(request.getPageCode())){
            return shareBoardReplyService.editReply(request, userInfo);
        } else if("review".equals(request.getPageCode())){
            return reviewBoardReplyService.editReply(request, userInfo);
        } else if("recipe".equals(request.getPageCode())){
            return recipeBoardReplyService.editReply(request, userInfo);
        }

        return new Result("9998", "올바르지 않은 요청 값입니다.");
    }
}

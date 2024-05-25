package com.unity.potato.service.board;

import com.unity.potato.domain.board.BoardInterface;
import com.unity.potato.domain.board.BoardLikeInterface;
import com.unity.potato.domain.board.BoardReplyInterface;
import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.recipe.RecipeBoard;
import com.unity.potato.domain.board.review.ReviewBoard;
import com.unity.potato.domain.board.share.ShareBoard;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.dto.BoardDTO;
import com.unity.potato.dto.BoardPageDTO;
import com.unity.potato.dto.request.*;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.board.free.FreeBoardService;
import com.unity.potato.service.board.recipe.RecipeBoardService;
import com.unity.potato.service.board.review.ReviewBoardService;
import com.unity.potato.service.board.share.ShareBoardService;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    @Autowired
    private FreeBoardService freeBoardService;
    @Autowired
    private ShareBoardService shareBoardService;
    @Autowired
    private ReviewBoardService reviewBoardService;
    @Autowired
    private RecipeBoardService recipeBoardService;

    @Transactional
    public Result uploadBoard(PostRequest request, UserInfo userInfo){
        if("free".equals(request.getPageCode())){
            return freeBoardService.uploadBoard(request, userInfo);
        } else if("share".equals(request.getPageCode())){
            return shareBoardService.uploadBoard(request, userInfo);
        } else if("review".equals(request.getPageCode())){
            return reviewBoardService.uploadBoard(request, userInfo);
        } else if("recipe".equals(request.getPageCode())){
            return recipeBoardService.uploadBoard(request, userInfo);
        }

        return new Result("9997", "잘못된 요청입니다.");
    }

    @Transactional
    public Result deletePost(PostDeleteRequest request, UserInfo userInfo){
        if("free".equals(request.getPageCode())){
            return freeBoardService.deleteFreeBoard(request.getId(), userInfo.getUserId());
        } else if("share".equals(request.getPageCode())){
            return shareBoardService.deleteShareBoard(request.getId(), userInfo.getUserId());
        } else if("review".equals(request.getPageCode())){
            return reviewBoardService.deleteReviewBoard(request.getId(), userInfo.getUserId());
        } else if("recipe".equals(request.getPageCode())){
            return recipeBoardService.deleteRecipeBoard(request.getId(), userInfo.getUserId());
        }

        return new Result("9997", "잘못된 요청입니다.");
    }

    @Transactional
    public Result updateBoard(PostEditRequest request, UserInfo userInfo){
        if(!StringUtil.isNumberic(request.getId())){
            return new Result("9999", "올바르지 않은 요청 값입니다.");
        }

        if("free".equals(request.getPageCode())){
            return freeBoardService.updateBoard(request, userInfo);
        } else if("share".equals(request.getPageCode())){
            return shareBoardService.updateBoard(request, userInfo);
        } else if("review".equals(request.getPageCode())){
            return reviewBoardService.updateBoard(request, userInfo);
        } else if("recipe".equals(request.getPageCode())){
            return recipeBoardService.updateBoard(request, userInfo);
        }

        return new Result("9999", "올바르지 않은 요청 값입니다.");
    }

    @Transactional
    public int getWriteCount(Long userId){
        return freeBoardService.getWriteCnt(userId)
                + shareBoardService.getWriteCnt(userId)
                + reviewBoardService.getWriteCnt(userId)
                + recipeBoardService.getWriteCnt(userId);
    }

    @Transactional
    public int getReplyCount(Long userId){
        return freeBoardService.getReplyCnt(userId)
                + shareBoardService.getReplyCnt(userId)
                + reviewBoardService.getReplyCnt(userId)
                + recipeBoardService.getReplyCnt(userId);
    }

    @Transactional
    public BoardPageDTO getBoardList(String category, int page, Long userId){
        if("write".equals(category)){
            return getWriteBoardList(page, userId);
        } else if("like".equals(category)){
            return getLikeBoardList(page, userId);
        } else if("reply".equals(category)){
            return getReplyBoardList(page, userId);
        }

        return null;
    }

    private String getBoardUrl(String boardType, Long id, String region){
        if("free".equals(boardType)){
            return "/community/free/" + id;
        } else if("share".equals(boardType)){
            return "/community/share/" + id;
        } else if("review".equals(boardType)){
            return "/community/review/" + region + "/" + id;
        } else if("recipe".equals(boardType)){
            return "/community/recipe/" + id;
        }
        return null;
    }

    public BoardPageDTO getWriteBoardList(int page, Long userId){
        List<BoardInterface> boardList = freeBoardService.getAllWriteBoard(userId, page);

        List<BoardDTO> boardDTOList = boardList.stream()
                .map(boardInterface -> {
                    BoardDTO boardDTO = BoardDTO.builder()
                            .url(getBoardUrl(boardInterface.getBoardType(), boardInterface.getId(), boardInterface.getRegion()))
                            .title(boardInterface.getTitle())
                            .nickname(boardInterface.getNickname())
                            .read_cnt(boardInterface.getReadCnt())
                            .reply_cnt(boardInterface.getReplyCnt())
                            .img_yn(boardInterface.getImgYn())
                            .reg_dt(boardInterface.getRegDt())
                            .build();

                    return boardDTO;
                })
                .collect(Collectors.toList());

        int writeCnt = freeBoardService.getWriteCount(userId);
        boolean hasNext = (writeCnt - (page+1)*10 > 0);

        return new BoardPageDTO(boardDTOList, page, hasNext);
    }

    public BoardPageDTO getReplyBoardList(int page, Long userId){
        List<BoardReplyInterface> replyList = freeBoardService.getReplyBoardList(userId, page);

        List<BoardDTO> replyDTOList = replyList.stream()
                .map(boardReplyInterface -> {
                    if("free".equals(boardReplyInterface.getBoardType())){
                        FreeBoard board = freeBoardService.getBoardInfo(boardReplyInterface.getPostId());
                        if(board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/free/" + board.getPostId() + "#reply" + boardReplyInterface.getId())
                                    .board_type("free")
                                    .title(board.getTitle())
                                    .content(boardReplyInterface.getContent())
                                    .reg_dt(boardReplyInterface.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    } else if("share".equals(boardReplyInterface.getBoardType())){
                        ShareBoard board = shareBoardService.getBoardInfo(boardReplyInterface.getPostId());
                        if(board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/share/" + board.getPostId() + "#reply" + boardReplyInterface.getId())
                                    .board_type("share")
                                    .title(board.getTitle())
                                    .content(boardReplyInterface.getContent())
                                    .reg_dt(boardReplyInterface.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    } else if("review".equals(boardReplyInterface.getBoardType())){
                        ReviewBoard board = reviewBoardService.getBoardInfo(boardReplyInterface.getPostId());
                        if(board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/review/" + board.getRegion() + "/" + board.getPostId() + "#reply" + boardReplyInterface.getId())
                                    .board_type("review")
                                    .title(board.getTitle())
                                    .content(boardReplyInterface.getContent())
                                    .reg_dt(boardReplyInterface.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    } else if("recipe".equals(boardReplyInterface.getBoardType())){
                        RecipeBoard board = recipeBoardService.getBoardInfo(boardReplyInterface.getPostId());
                        if(board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/recipe/" + board.getPostId() + "#reply" + boardReplyInterface.getId())
                                    .board_type("recipe")
                                    .title(board.getTitle())
                                    .content(boardReplyInterface.getContent())
                                    .reg_dt(boardReplyInterface.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    }
                    return null;
                })
                .collect(Collectors.toList());

        int replyBoardCnt = freeBoardService.getReplyBoardCnt(userId);
        boolean hasNext = (replyBoardCnt - (page+1)*10 > 0);

        return new BoardPageDTO(replyDTOList, page, hasNext);
    }

    public BoardPageDTO getLikeBoardList(int page, Long userId){
        List<BoardLikeInterface> likeList = freeBoardService.getLikeBoardList(userId, page);

        List<BoardDTO> boardDTOList = likeList.stream()
                .map(boardLikeInterface -> {
                    if("free".equals(boardLikeInterface.getBoardType())){
                        FreeBoard board = freeBoardService.getBoardInfo(boardLikeInterface.getId());
                        if(board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/free/" + board.getPostId())
                                    .title(board.getTitle())
                                    .nickname(board.getWriterNickname())
                                    .read_cnt(board.getReadCnt())
                                    .reply_cnt(board.getReplyCnt())
                                    .img_yn(board.getImgYn())
                                    .reg_dt(board.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    } else if("share".equals(boardLikeInterface.getBoardType())) {
                        ShareBoard board = shareBoardService.getBoardInfo(boardLikeInterface.getId());
                        if (board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/share/" + board.getPostId())
                                    .title(board.getTitle())
                                    .nickname(board.getWriterNickname())
                                    .read_cnt(board.getReadCnt())
                                    .reply_cnt(board.getReplyCnt())
                                    .img_yn(board.getImgYn())
                                    .reg_dt(board.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    } else if("review".equals(boardLikeInterface.getBoardType())){
                        ReviewBoard board = reviewBoardService.getBoardInfo(boardLikeInterface.getId());
                        if (board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/review/" + board.getRegion() + "/" + board.getPostId())
                                    .title(board.getTitle())
                                    .nickname(board.getWriterNickname())
                                    .read_cnt(board.getReadCnt())
                                    .reply_cnt(board.getReplyCnt())
                                    .img_yn(board.getImgYn())
                                    .reg_dt(board.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    } else if("recipe".equals(boardLikeInterface.getBoardType())){
                        RecipeBoard board = recipeBoardService.getBoardInfo(boardLikeInterface.getId());
                        if (board != null) {
                            BoardDTO boardDTO = BoardDTO.builder()
                                    .url("/community/recipe/" + board.getPostId())
                                    .title(board.getTitle())
                                    .nickname(board.getWriterNickname())
                                    .read_cnt(board.getReadCnt())
                                    .reply_cnt(board.getReplyCnt())
                                    .img_yn(board.getImgYn())
                                    .reg_dt(board.getRegDt())
                                    .build();
                            return boardDTO;
                        }
                    }
                    return null;
                })
                .collect(Collectors.toList());

        int likeBoardCnt = freeBoardService.getLikeBoardCnt(userId);
        boolean hasNext = (likeBoardCnt - (page+1)*10 > 0);

        return new BoardPageDTO(boardDTOList, page, hasNext);
    }
}

package com.unity.potato.service.board.free;

import com.unity.potato.domain.board.BoardInterface;
import com.unity.potato.domain.board.BoardLikeInterface;
import com.unity.potato.domain.board.BoardReplyInterface;
import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardLikeRepository;
import com.unity.potato.domain.board.free.FreeBoardReplyRepository;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.board.share.ShareBoard;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.vo.BoardVo;
import com.unity.potato.dto.BoardDTO;
import com.unity.potato.dto.BoardPageDTO;
import com.unity.potato.dto.request.PostEditRequest;
import com.unity.potato.dto.request.PostRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.service.user.UserService;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardService {

    @Autowired
    private FreeBoardRepository freeBoardRepository;
    @Autowired
    private FreeBoardLikeService freeBoardLikeService;
    @Autowired
    private UserService userService;
    @Autowired
    private FreeBoardReplyRepository freeBoardReplyRepository;
    @Autowired
    private FreeBoardLikeRepository freeBoardLikeRepository;

    @Transactional
    public Page<BoardVo> getBoardList(String page, String sort) {
        Page<BoardVo> freeBoardList = null;

        if(StringUtil.isNullOrEmpty(sort)){ // 최신 글 순서대로
            freeBoardList = getRecentBoardList(Integer.parseInt(page), "regDt");
        } else if("popular".equals(sort)){
            freeBoardList = getPopularBoardList(Integer.parseInt(page));
        }

        return freeBoardList;
    }

    private Page<BoardVo> getRecentBoardList(int page, String sort){
        Pageable pageable = PageRequest.of(page,10, Sort.by(Sort.Direction.DESC, sort));
        return freeBoardRepository.findAllByDeleteYnNot('Y',pageable);
    }

    private Page<BoardVo> getPopularBoardList(int page){
        Pageable popularPageable = PageRequest.of(page, 10,
                Sort.by(Sort.Direction.DESC, "likeCnt"));

        return freeBoardRepository.findPopularByDeleteYnNot('Y', popularPageable);
    }

    public Result uploadBoard(PostRequest request, UserInfo userInfo){
        FreeBoard board = FreeBoard.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writerId(userInfo.getUserId())
                .writerNickname(userInfo.getUserNickname())
                .imgYn(request.getImgYn())
                .regDt(LocalDateTime.now())
                .build();

        FreeBoard uploadBoard = freeBoardRepository.save(board);

        if(uploadBoard != null && uploadBoard.getPostId() != null) {
            return new Result("0000", "작성 완료", board.getPostId());
        } else {
            return new Result("9998", "글 업로드 실패");
        }
    }

    public Result updateBoard(PostEditRequest request, UserInfo userInfo){
        FreeBoard board = freeBoardRepository.findById(Long.valueOf(request.getId())).orElse(null);

        if(board != null) {
            if(isBoardOwner(board.getWriterId(), userInfo.getUserId())){
                updateFreeBoard(request, board);
                return new Result("0000", "수정 완료", board.getPostId());
            } else {
                return new Result("9996", "본인이 작성한 글만 수정할 수 있습니다.");
            }
        } else {
            return new Result("9998", "존재하지 않은 포스트입니다.");
        }
    }

    @Transactional
    public void updateReadCnt(FreeBoard board){
        board.increaseReadCnt();
        freeBoardRepository.save(board);
    }

    public Result deleteFreeBoard(Long id, Long userId){
        FreeBoard board = freeBoardRepository.findById(id).orElse(null);
        if(board != null){
            if(isBoardOwner(board.getWriterId(), userId)){
                if('Y' == board.getDeleteYn()){
                    return new Result("9997", "이미 삭제된 포스트입니다.");
                } else {
                    // 댓글, 좋아요, 글 삭제
                    freeBoardReplyRepository.deleteAllByPostId(board.getPostId());
                    freeBoardLikeRepository.deleteAllByPostId(board.getPostId());

                    board.setDeleteYn('Y');
                    board.setDeleteDt(LocalDateTime.now());
                    freeBoardRepository.save(board);
                    return new Result("0000", "삭제 완료");
                }
            } else {
                return new Result("9996", "본인이 작성한 글만 삭제할 수 있습니다.");
            }
        } else {
            return new Result("9998", "존재하지 않은 포스트입니다.");
        }
    }

    public void updateFreeBoard(PostEditRequest request, FreeBoard board){
        FreeBoard updateBoard = board.toBuilder()
                .title(request.getTitle())
                .content(request.getContent())
                .imgYn(request.getImgYn())
                .updateDt(LocalDateTime.now())
                .build();

        freeBoardRepository.save(updateBoard);
    }

    public boolean isBoardOwner(Long writerId, Long userId){
        if(writerId.equals(userId)){
            return true;
        }
        return false;
    }

    @Transactional
    public void getBoardInfo(String id, UserInfo userInfo, Model model){
        FreeBoard board = freeBoardRepository.findById(Long.valueOf(id)).orElse(null);

        if(board != null) {
            boolean isLiked = freeBoardLikeService.getBoardLike(board.getPostId(), userInfo.getUserId());
            if('Y' == board.getDeleteYn()){
                model.addAttribute("error", "삭제된 게시글입니다.");
            } else {
                model.addAttribute("board", board);
                model.addAttribute("writerProfileImg", userService.getWriterProfileImg(board.getWriterId()));
                model.addAttribute("isLiked", isLiked);
                if(isBoardOwner(board.getWriterId(), userInfo.getUserId())){
                    model.addAttribute("isOwner", true);
                } else {
                    updateReadCnt(board);
                }
            }
        } else {
            model.addAttribute("error", "유효하지 않은 URL입니다.");
        }
    }

    @Transactional
    public void getBoardEditInfo(String id, UserInfo userInfo, Model model){
        FreeBoard board = freeBoardRepository.findById(Long.valueOf(id)).orElse(null);
        if(board != null) {
            if(userInfo.getUserId().equals(board.getWriterId())){
                model.addAttribute("board", board);
            } else {
                model.addAttribute("error", "유효하지 않은 ID입니다");
            }
        }
    }

    public int getWriteCnt(Long userId){
        return freeBoardRepository.countByWriterId(userId);
    }

    public int getReplyCnt(Long userId){
        return freeBoardReplyRepository.countByUserId(userId);
    }

    public List<BoardInterface> getAllWriteBoard(Long userId, int page){
        return freeBoardRepository.findAllWriteBoard(userId, 10, 10*page);
    }

    public int getWriteCount(Long userId){
        return freeBoardRepository.countWriteBoard(userId);
    }

    public List<BoardLikeInterface> getLikeBoardList(Long userId, int page){
        return freeBoardLikeRepository.findAllLikeBoard(userId, 10, page*10);
    }

    public List<BoardReplyInterface> getReplyBoardList(Long userId, int page){
        return freeBoardReplyRepository.findAllReplyBoard(userId, 10, page*10);
    }

    public int getLikeBoardCnt(Long userId){
        return freeBoardLikeRepository.countLikeBoard(userId);
    }

    public int getReplyBoardCnt(Long userId){
        return freeBoardReplyRepository.countReplyBoard(userId);
    }

    public FreeBoard getBoardInfo(Long postId) {
        return freeBoardRepository.findByPostId(postId).orElse(null);
    }

}

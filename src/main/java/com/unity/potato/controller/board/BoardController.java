package com.unity.potato.controller.board;

import com.unity.potato.domain.board.BoardInterface;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.dto.BoardDTO;
import com.unity.potato.dto.BoardPageDTO;
import com.unity.potato.dto.request.*;
import com.unity.potato.dto.response.Result;
import com.unity.potato.security.CurrentUser;
import com.unity.potato.service.board.BoardLikeService;
import com.unity.potato.service.board.BoardReplyService;
import com.unity.potato.service.board.BoardService;
import com.unity.potato.util.StringUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    @Autowired
    private BoardService boardService;
    @Autowired
    private BoardLikeService boardLikeService;
    @Autowired
    private BoardReplyService boardReplyService;

    @PostMapping("/api/post-upload")
    public ResponseEntity<?> uploadBoard(@Valid @RequestBody PostRequest request, Errors errors, @CurrentUser UserInfo userInfo){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9995", "유효성 검증에 실패하였습니다. 규격에 맞춰 제출해주세요."));
        }

        try {
            Result result = boardService.uploadBoard(request, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }

    @PostMapping("/api/post-delete")
    public ResponseEntity<?> deletePost(@Valid @RequestBody PostDeleteRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9995", "유효성 검증에 실패하였습니다. 규격에 맞춰 제출해주세요."));
        }

        try {
            Result result = boardService.deletePost(request, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }

    @PostMapping("/api/post-update")
    public ResponseEntity<?> updatePost(@Valid @RequestBody PostEditRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9995", "유효성 검증에 실패하였습니다. 규격에 맞춰 제출해주세요."));
        }

        try {
            Result result = boardService.updateBoard(request, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }

    @PostMapping("/api/post-like")
    public ResponseEntity<?> likePost(@Valid @RequestBody PostLikeRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9995", "유효성 검증에 실패하였습니다. 규격에 맞춰 제출해주세요."));
        }

        try {
            Result result = boardLikeService.updateBoardLike(request, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청 값입니다."));
        }
    }

    @PostMapping("/api/post-unlike")
    public ResponseEntity<?> unlikePost(@Valid @RequestBody PostLikeRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9995", "유효성 검증에 실패하였습니다. 규격에 맞춰 제출해주세요."));
        }

        try {
            Result result = boardLikeService.updateBoardUnlike(request, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청 값입니다."));
        }
    }

    @PostMapping("/api/post/reply-upload")
    public ResponseEntity<?> uploadReply(@Valid @RequestBody ReplyRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        try {
            if(errors.hasErrors()){
                return ResponseEntity.ok(new Result("9995", "유효성 검증에 실패하였습니다. 규격에 맞춰 제출해주세요."));
            }
            Result result = boardReplyService.uploadReply(request, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e){
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }

    @PostMapping("/api/post/reply")
    public ResponseEntity<?> getReply(@Valid @RequestBody ReplyListRequest request, @CurrentUser UserInfo userInfo, Errors errors) {
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청 값입니다."));
        }

        try {
            List<Map<String, Object>> replyMap = boardReplyService.getReplyList(request, userInfo);
            return ResponseEntity.ok(new Result("0000", "ok", replyMap));
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }

    @PostMapping("/api/post/reply-delete")
    public ResponseEntity<?> deleteReply(@Valid @RequestBody ReplyDeleteRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청 값입니다."));
        }

        try {
            Result result = boardReplyService.deleteReply(request, userInfo.getUserId());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류 발생"));
        }
    }

    @PostMapping("/api/post/reply-edit")
    public ResponseEntity<?> editReply(@RequestBody ReplyEditRequest request, @CurrentUser UserInfo userInfo, Errors errors){
        if(errors.hasErrors()){
            return ResponseEntity.ok(new Result("9999", "올바르지 않은 요청 값입니다."));
        }

        try {
            Result result = boardReplyService.editReply(request, userInfo);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(new Result("9999", "처리 도중 오류가 발생하였습니다."));
        }
    }
}

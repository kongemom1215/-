package com.unity.potato.service.board.free;

import com.unity.potato.domain.board.BoardLikeInterface;
import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardLike;
import com.unity.potato.domain.board.free.FreeBoardLikeRepository;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.dto.request.PostLikeRequest;
import com.unity.potato.event.like.LikeCreatedEvent;
import com.unity.potato.event.like.LikeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardLikeService {

    @Autowired
    private FreeBoardRepository freeBoardRepository;
    @Autowired
    private FreeBoardLikeRepository freeBoardLikeRepository;

    private final ApplicationEventPublisher eventPublisher;

    public boolean getBoardLike(Long postId, Long userId){
        return freeBoardLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public void updateBoardLike(PostLikeRequest request, UserInfo userInfo){
        if(!getBoardLike(Long.valueOf(request.getPostId()), userInfo.getUserId())){
            FreeBoardLike like = likeBoard(Long.valueOf(request.getPostId()), userInfo.getUserId());
            updateBoardLikeCnt("up", request.getPostId());

            eventPublisher.publishEvent(new LikeCreatedEvent(like, LikeType.like));
        }
    }

    public FreeBoardLike likeBoard(Long postId, Long userId){
        FreeBoardLike freeBoardLike = FreeBoardLike.builder()
                .postId(postId)
                .userId(userId)
                .likeDt(LocalDateTime.now())
                .build();

        return freeBoardLikeRepository.save(freeBoardLike);
    }

    public void updateBoardLikeCnt(String type, String postId){
        FreeBoard board = freeBoardRepository.findById(Long.valueOf(postId)).orElse(null);
        if(board != null) {
            if("up".equals(type)){
                board.increaseLikeCnt();
                freeBoardRepository.save(board);
            } else if("down".equals(type)){
                board.decreaseLikeCnt();
                freeBoardRepository.save(board);
            }
        }
    }

    public void updateBoardUnLike(PostLikeRequest request, UserInfo userInfo){
        FreeBoardLike like = freeBoardLikeRepository.findByPostIdAndUserId(Long.valueOf(request.getPostId()), userInfo.getUserId()).orElse(null);
        if(like != null){
            unlikeBoard(like);
            updateBoardLikeCnt("down", request.getPostId());

            eventPublisher.publishEvent(new LikeCreatedEvent(like, LikeType.unLike));
        }
    }

    public void unlikeBoard(FreeBoardLike like){
        freeBoardLikeRepository.delete(like);
    }



}

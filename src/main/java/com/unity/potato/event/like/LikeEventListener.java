package com.unity.potato.event.like;

import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardLike;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.board.recipe.RecipeBoard;
import com.unity.potato.domain.board.recipe.RecipeBoardLike;
import com.unity.potato.domain.board.recipe.RecipeBoardRepository;
import com.unity.potato.domain.board.review.ReviewBoard;
import com.unity.potato.domain.board.review.ReviewBoardLike;
import com.unity.potato.domain.board.review.ReviewBoardRepository;
import com.unity.potato.domain.board.share.ShareBoard;
import com.unity.potato.domain.board.share.ShareBoardLike;
import com.unity.potato.domain.board.share.ShareBoardRepository;
import com.unity.potato.domain.notification.Notification;
import com.unity.potato.domain.notification.NotificationRepository;
import com.unity.potato.domain.notification.NotificationType;
import com.unity.potato.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
@Transactional
public class LikeEventListener {

    private final FreeBoardRepository freeBoardRepository;
    private final RecipeBoardRepository recipeBoardRepository;
    private final ReviewBoardRepository reviewBoardRepository;
    private final ShareBoardRepository shareBoardRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleLikeCreatedEvent(LikeCreatedEvent event){
        if(event.getFreeBoardLike() != null){
            saveFreeBoardLikeNotification(event.getFreeBoardLike(), event.getLikeType());
        } else if(event.getRecipeBoardLike() != null){
            ...
        } else if(event.getReviewBoardLike() != null){
            ...
        } else if(event.getShareBoardLike() != null){
            ...
        }
    }

    private void saveFreeBoardLikeNotification(FreeBoardLike like, LikeType likeType){
        FreeBoard board = freeBoardRepository.findByPostId(like.getPostId()).orElse(null);
        if(board != null && !like.getUserId().equals(board.getWriterId())) {
            Notification existNoti = notificationRepository.findByUserIdAndLinkAndType(board.getWriterId(), "/community/free/" + board.getPostId(), NotificationType.like).orElse(null);

            if (existNoti == null) {
                if(likeType == LikeType.like){
                    Notification noti = Notification.builder()
                            .userId(board.getWriterId())
                            .type(NotificationType.like)
                            .link("/community/free/" + board.getPostId())
                            .title(StringUtil.truncateString(board.getTitle(), 20))
                            .alarmCnt(1)
                            .isChecked(false)
                            .regDt(LocalDateTime.now())
                            .build();
                    notificationRepository.save(noti);
                }
            } else {
                if(likeType == LikeType.like){
                    existNoti.increaseAlarmCnt();
                    existNoti.setRegDt(LocalDateTime.now());
                    existNoti.setChecked(false);
                    notificationRepository.save(existNoti);
                } else if(likeType == LikeType.unLike){
                    existNoti.decreaseAlarmCnt();
                    existNoti.setRegDt(LocalDateTime.now());
                    existNoti.setChecked(false);
                    Notification update = notificationRepository.save(existNoti);
                }
            }
        }
    }

}

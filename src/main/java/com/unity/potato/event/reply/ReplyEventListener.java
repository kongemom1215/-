package com.unity.potato.event.reply;

import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardReply;
import com.unity.potato.domain.board.free.FreeBoardReplyRepository;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.board.recipe.RecipeBoard;
import com.unity.potato.domain.board.recipe.RecipeBoardReply;
import com.unity.potato.domain.board.recipe.RecipeBoardReplyRepository;
import com.unity.potato.domain.board.recipe.RecipeBoardRepository;
import com.unity.potato.domain.board.review.ReviewBoard;
import com.unity.potato.domain.board.review.ReviewBoardReply;
import com.unity.potato.domain.board.review.ReviewBoardReplyRepository;
import com.unity.potato.domain.board.review.ReviewBoardRepository;
import com.unity.potato.domain.board.share.ShareBoard;
import com.unity.potato.domain.board.share.ShareBoardReply;
import com.unity.potato.domain.board.share.ShareBoardReplyRepository;
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
public class ReplyEventListener {

    private final FreeBoardRepository freeBoardRepository;
    private final FreeBoardReplyRepository freeBoardReplyRepository;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleReplyCreatedEvent(ReplyCreatedEvent event){
        if(event.getFreeBoardReply() != null) {
            saveFreeBoardReplyNotification(event.getFreeBoardReply());
        } else if(event.getReviewBoardReply() != null) {
            ...
        } else if(event.getRecipeBoardReply() != null) {
            ...
        } else if(event.getShareBoardReply() != null) {
            ...
        }
    }

    private void saveFreeBoardReplyNotification(FreeBoardReply reply){
        FreeBoard board = freeBoardRepository.findByPostId(reply.getPostId()).orElse(null);

        if(board != null){
            Long writerId = board.getWriterId();
            Long replyUserId = reply.getUserId();
            Long parentId = reply.getParentId();
            Long targetId = reply.getTargetId();

            // 글 작성자 댓글 알림
            if(!replyUserId.equals(writerId)){
                Notification writerNoti = notificationRepository.findByUserIdAndLinkAndType(writerId, "/community/free/" + board.getPostId(),  NotificationType.reply).orElse(null);
                sendNotification(writerNoti, board);
            }

            // 답글 알림
            if(parentId != null){
                FreeBoardReply parentReply = freeBoardReplyRepository.findById(parentId).orElse(null);

                if(parentReply != null && !replyUserId.equals(parentReply.getUserId()) && !parentReply.getUserId().equals(writerId)){
                    Notification parentNoti = notificationRepository.findByUserIdAndLinkAndType(parentReply.getUserId(), "/community/free/" + board.getPostId() + "#reply" + reply.getId(),  NotificationType.re_reply).orElse(null);
                    sendParentNotification(parentReply.getUserId(), parentNoti, board, reply);
                }

                if(targetId != null){
                    FreeBoardReply targetReply = freeBoardReplyRepository.findById(targetId).orElse(null);
                    if(parentReply != null && targetReply != null && !parentReply.getUserId().equals(targetReply.getUserId()) && !targetReply.getUserId().equals(writerId)) {
                        Notification targetNoti = notificationRepository.findByUserIdAndLinkAndType(targetReply.getUserId(), "/community/free/" + board.getPostId() + "#reply" + reply.getId(),  NotificationType.re_reply).orElse(null);
                        sendTargetNotification(targetReply.getUserId(), targetNoti, board, reply);
                    }
                }
            }
        }
    }



    private void sendNotification(Notification noti, FreeBoard board){
        if(noti == null){
            Notification newNoti = Notification.builder()
                    .userId(board.getWriterId())
                    .type(NotificationType.reply)
                    .link("/community/free/" + board.getPostId())
                    .title(StringUtil.truncateString(board.getTitle(), 20))
                    .alarmCnt(1)
                    .isChecked(false)
                    .regDt(LocalDateTime.now())
                    .build();
            notificationRepository.save(newNoti);
        } else {
            noti.increaseAlarmCnt();
            noti.setRegDt(LocalDateTime.now());
            noti.setChecked(false);
            notificationRepository.save(noti);
        }
    }


    private void sendParentNotification(Long parentUserId, Notification noti, FreeBoard board, FreeBoardReply reply){
        if(noti == null){
            Notification newNoti = Notification.builder()
                    .userId(parentUserId)
                    .type(NotificationType.re_reply)
                    .link("/community/free/" + board.getPostId() + "#reply" + reply.getId())
                    .title(StringUtil.truncateString(reply.getContent(), 20))
                    .alarmCnt(1)
                    .isChecked(false)
                    .regDt(LocalDateTime.now())
                    .build();
            notificationRepository.save(newNoti);
        } else {
            noti.increaseAlarmCnt();
            noti.setRegDt(LocalDateTime.now());
            noti.setChecked(false);
            notificationRepository.save(noti);
        }
    }


    private void sendTargetNotification(Long targetUserId, Notification noti, FreeBoard board, FreeBoardReply reply){
        if(noti == null){
            Notification newNoti = Notification.builder()
                    .userId(targetUserId)
                    .type(NotificationType.re_reply)
                    .link("/community/free/" + board.getPostId() + "#reply" + reply.getId())
                    .title(StringUtil.truncateString(reply.getContent(), 20))
                    .alarmCnt(1)
                    .isChecked(false)
                    .regDt(LocalDateTime.now())
                    .build();
            notificationRepository.save(newNoti);
        } else {
            noti.increaseAlarmCnt();
            noti.setRegDt(LocalDateTime.now());
            noti.setChecked(false);
            notificationRepository.save(noti);
        }
    }


}

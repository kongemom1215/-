package com.unity.potato.service.board.free;

import com.unity.potato.domain.board.free.FreeBoard;
import com.unity.potato.domain.board.free.FreeBoardReply;
import com.unity.potato.domain.board.free.FreeBoardReplyRepository;
import com.unity.potato.domain.board.free.FreeBoardRepository;
import com.unity.potato.domain.user.UserInfo;
import com.unity.potato.domain.user.UserInfoRepository;
import com.unity.potato.domain.user.UserProfile;
import com.unity.potato.dto.request.ReplyEditRequest;
import com.unity.potato.dto.request.ReplyRequest;
import com.unity.potato.dto.response.Result;
import com.unity.potato.event.reply.ReplyCreatedEvent;
import com.unity.potato.event.reply.ReplyEventListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FreeBoardReplyService {
    @Autowired
    private FreeBoardRepository freeBoardRepository;
    @Autowired
    private FreeBoardReplyRepository freeBoardReplyRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    private final ApplicationEventPublisher eventPublisher;

    public void updateBoardReplyCnt(Long postId, String type){
        FreeBoard board = freeBoardRepository.findByPostId(postId).orElse(null);
        if(board != null){
            if("up".equals(type)){
                board.increaseReplyCnt();
            } else if("down".equals(type)){
                board.decreaseReplyCnt();
            }
        }
    }

    public Result uploadReply(ReplyRequest request, UserInfo userInfo){
        if(!freeBoardRepository.existsById(Long.valueOf(request.getPostId()))){
            return new Result("9990", "없거나 삭제된 게시물입니다.");
        }

        FreeBoardReply reply = setBoardReply(request, userInfo);

        if(!checkReplyParent(reply)){ // 삭제된 댓글에 댓글달 수 없음
            return new Result("9991", "없거나 삭제된 댓글입니다.");
        }
        updateBoardReplyCnt(Long.valueOf(request.getPostId()), "up");
        freeBoardReplyRepository.save(reply);

        eventPublisher.publishEvent(new ReplyCreatedEvent(reply));

        return new Result("0000", "댓글이 업로드 되었습니다.");
    }

    private FreeBoardReply setBoardReply(ReplyRequest request, UserInfo userInfo){
        FreeBoardReply reply = FreeBoardReply.builder()
                .postId(Long.valueOf(request.getPostId()))
                .content(Jsoup.clean(request.getContent(), Safelist.none()))
                .userId(userInfo.getUserId())
                .userName(userInfo.getUserNickname())
                .regDt(LocalDateTime.now())
                .parentId(request.getParentId() != null ? request.getParentId() : null)
                .targetId(request.getTargetId() != null ? request.getTargetId() : null)
                .build();

        return reply;
    }

    private boolean checkReplyParent(FreeBoardReply reply){
        // 대댓글일 경우 target 댓글이 있는지 체크
        if(reply.getParentId() != null) {
            FreeBoardReply parentReply = freeBoardReplyRepository.findById(reply.getParentId()).orElse(null);
            if(parentReply == null) {
                return false;
            }
            if(reply.getTargetId() != null){
                FreeBoardReply targetReply = freeBoardReplyRepository.findById(reply.getTargetId()).orElse(null);
                if(targetReply == null){
                    return false;
                }
            }
        }

        return true;
    }

    public List<Map<String, Object>> formmatedReplyList(List<FreeBoardReply> replyList, UserInfo userInfo){
        List<Map<String, Object>> formattedReplyList = replyList.stream()
                .filter(reply -> reply != null && reply.getParentId() == null) // null 체크
                .map(reply -> {
                    Map<String, Object> formattedReply = new HashMap<>();
                    formattedReply.put("id", reply.getId() != null ? reply.getId() : "");
                    formattedReply.put("post_id", reply.getPostId() != null ? reply.getPostId() : "");
                    UserInfo replyUser = userInfoRepository.findById(reply.getUserId()).orElse(null);
                    if(replyUser != null){
                        UserProfile profile = replyUser.getUserProfile();
                        formattedReply.put("profile_img", profile.getUserProfileImg());
                    } else {
                        formattedReply.put("profile_img", null);
                    }
                    formattedReply.put("user_id", reply.getUserId() != null ? reply.getUserId() : "");
                    formattedReply.put("user_name", reply.getUserName() != null ? reply.getUserName() : "");
                    formattedReply.put("content", reply.getContent() != null ? reply.getContent() : "");
                    formattedReply.put("reg_dt", reply.getRegDt() != null ? reply.getRegDt() : "");
                    formattedReply.put("update_dt", reply.getUpdateDt() != null ? reply.getUpdateDt() : "");
                    formattedReply.put("is_owner", reply.getUserId().equals(userInfo.getUserId()));

                    // 자식 댓글(children) 정보 추가
                    List<Map<String, Object>> childrenList = replyList.stream()
                            .filter(child -> child.getParentId() != null && child.getParentId().equals(reply.getId()))
                            .map(child -> {
                                Map<String, Object> childMap = new HashMap<>();
                                childMap.put("id", child.getId() != null ? child.getId() : "");
                                childMap.put("post_id", child.getPostId() != null ? child.getPostId() : "");
                                UserInfo childUser = userInfoRepository.findById(child.getUserId()).orElse(null);
                                if(childUser != null){
                                    UserProfile profile = childUser.getUserProfile();
                                    childMap.put("profile_img", profile.getUserProfileImg());
                                } else {
                                    childMap.put("profile_img", null);
                                }
                                childMap.put("user_id", child.getUserId() != null ? child.getUserId() : "");
                                childMap.put("user_name", child.getUserName() != null ? child.getUserName() : "");
                                childMap.put("content", child.getContent() != null ? child.getContent() : "");
                                childMap.put("mentioned_user_name", child.getTargetId() != null ? freeBoardReplyRepository.findUserNameById(child.getTargetId()) : null);
                                childMap.put("parent_id", child.getParentId() != null ? child.getParentId() : "");
                                childMap.put("reg_dt", child.getRegDt() != null ? child.getRegDt() : "");
                                childMap.put("update_dt", child.getUpdateDt() != null ? child.getUpdateDt() : "");
                                childMap.put("is_owner", child.getUserId().equals(userInfo.getUserId()));
                                return childMap;
                            })
                            .collect(Collectors.toList());

                    formattedReply.put("children", childrenList);

                    return formattedReply;
                })
                .collect(Collectors.toList());

        return formattedReplyList;
    }

    public Result deleteReply(Long replyId, Long userId){
            //댓글이 존재하는지 확인
            FreeBoardReply reply = freeBoardReplyRepository.findById(replyId).orElse(null);
            if(reply == null || reply.getDeleteDt() != null){
                return new Result("9997", "존재하지 않는 댓글입니다.");
            }
            //본인인지 확인
            if(!userId.equals(reply.getUserId())){
                return new Result("9996", "본인이 작성한 댓글만 삭제할 수 있습니다.");
            }
            //답글이 존재하는지 확인
            if(isExistChildReply(reply)){
                return new Result("9995", "대댓글이 있는 댓글은 삭제할 수 없습니다.");
            }

            //삭제
            reply.setDeleteDt(LocalDateTime.now());
            freeBoardReplyRepository.save(reply);
            updateBoardReplyCnt(reply.getPostId(), "down");
            return new Result("0000", "댓글 정상 삭제");
    }

    private boolean isExistChildReply(FreeBoardReply reply){
        if(reply.getParentId() == null){
            List<FreeBoardReply> replyList = freeBoardReplyRepository.findAllByParentIdAndDeleteDtNull(reply.getId());
            if(!replyList.isEmpty()){
                return true;
            }
        }
        return false;
    }

    public Result editReply(ReplyEditRequest request, UserInfo userInfo){
        //댓글 있는지 확인
        FreeBoardReply reply = freeBoardReplyRepository.findById(request.getId()).orElse(null);
        if(reply == null){
            return new Result("9997", "존재하지 않는 댓글입니다.");
        }
        //본인 댓글인지 확인
        if(!reply.getUserId().equals(userInfo.getUserId())){
            return new Result("9996", "본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        reply.setContent(Jsoup.clean(request.getContent(), Safelist.none()));
        reply.setUpdateDt(LocalDateTime.now());

        freeBoardReplyRepository.save(reply);

        return new Result("0000", "수정완료");
    }
}

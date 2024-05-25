package com.unity.potato.event.reply;

import com.unity.potato.domain.board.free.FreeBoardReply;
import com.unity.potato.domain.board.recipe.RecipeBoardReply;
import com.unity.potato.domain.board.review.ReviewBoardReply;
import com.unity.potato.domain.board.share.ShareBoardReply;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@Getter
@Setter
public class ReplyCreatedEvent extends ApplicationEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private FreeBoardReply freeBoardReply;
    private ShareBoardReply shareBoardReply;
    private RecipeBoardReply recipeBoardReply;
    private ReviewBoardReply reviewBoardReply;

    public ReplyCreatedEvent(FreeBoardReply reply){
        super(reply);
        this.freeBoardReply = reply;
    }
    public ReplyCreatedEvent(ShareBoardReply reply){
        super(reply);
        this.shareBoardReply = reply;
    }
    public ReplyCreatedEvent(RecipeBoardReply reply){
        super(reply);
        this.recipeBoardReply = reply;
    }
    public ReplyCreatedEvent(ReviewBoardReply reply){
        super(reply);
        this.reviewBoardReply = reply;
    }
}

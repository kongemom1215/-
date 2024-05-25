package com.unity.potato.event.like;

import com.unity.potato.domain.board.free.FreeBoardLike;
import com.unity.potato.domain.board.recipe.RecipeBoardLike;
import com.unity.potato.domain.board.review.ReviewBoardLike;
import com.unity.potato.domain.board.share.ShareBoardLike;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.io.Serializable;

@Getter @Setter
public class LikeCreatedEvent extends ApplicationEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private FreeBoardLike freeBoardLike;
    private ShareBoardLike shareBoardLike;
    private RecipeBoardLike recipeBoardLike;
    private ReviewBoardLike reviewBoardLike;
    private LikeType likeType;

    public LikeCreatedEvent(FreeBoardLike like, LikeType likeType) {
        super(like);
        this.freeBoardLike = like;
        this.likeType = likeType;
    }

    public LikeCreatedEvent(ShareBoardLike like, LikeType likeType) {
        super(like);
        this.shareBoardLike = like;
        this.likeType = likeType;
    }

    public LikeCreatedEvent(RecipeBoardLike like, LikeType likeType) {
        super(like);
        this.recipeBoardLike = like;
        this.likeType = likeType;
    }

    public LikeCreatedEvent(ReviewBoardLike like, LikeType likeType) {
        super(like);
        this.reviewBoardLike = like;
        this.likeType = likeType;
    }

}

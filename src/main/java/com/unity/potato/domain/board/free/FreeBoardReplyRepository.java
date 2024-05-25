package com.unity.potato.domain.board.free;

import com.unity.potato.domain.board.BoardReplyInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FreeBoardReplyRepository extends JpaRepository<FreeBoardReply, Long> {

    Optional<FreeBoardReply> findById(Long id);

    List<FreeBoardReply> findAllByPostIdAndDeleteDtNull(Long postId);

    List<FreeBoardReply> findAllByParentIdAndDeleteDtNull(Long parentId);

    @Query("SELECT r.userName FROM FreeBoardReply r WHERE r.id = :id")
    String findUserNameById(@Param("id") Long id);

    int countByUserId(Long userId);

    void deleteAllByPostId(Long postId);

    @Query(value = "SELECT board_type as boardType, id, post_id as postId, content, reg_dt as regDt FROM (" +
            "   SELECT 'free' AS board_type, id, post_id, content, reg_dt " +
            "   FROM free_board_reply " +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 'share' AS board_type, id, post_id, content, reg_dt " +
            "   FROM share_board_reply " +
            "   WHERE user_id = :userId " +
            "   UNION ALL " +
            "   SELECT 'review' AS board_type, id, post_id, content, reg_dt " +
            "   FROM review_board_reply" +
            "   WHERE user_id = :userId " +
            "   UNION ALL " +
            "   SELECT 'recipe' AS board_type, id, post_id, content, reg_dt " +
            "   FROM recipe_board_reply " +
            "   WHERE user_id = :userId " +
            ") AS reply_board " +
            "ORDER BY reg_dt DESC " +
            "LIMIT :pageSize OFFSET :offset",
            nativeQuery = true)
    List<BoardReplyInterface> findAllReplyBoard(@Param("userId") Long userId, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Query(value = "SELECT count(*) FROM (" +
            "   SELECT 1 " +
            "   FROM free_board_reply" +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 1 " +
            "   FROM share_board_reply" +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 1 " +
            "   FROM review_board_reply" +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 1 " +
            "   FROM recipe_board_reply" +
            "   WHERE user_id = :userId " +
            ") AS count_like ", nativeQuery = true)
    int countReplyBoard(@Param("userId") Long userId);
}

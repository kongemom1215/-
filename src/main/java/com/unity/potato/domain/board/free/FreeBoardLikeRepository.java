package com.unity.potato.domain.board.free;

import com.unity.potato.domain.board.BoardLikeInterface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FreeBoardLikeRepository extends JpaRepository<FreeBoardLike, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    Optional<FreeBoardLike> findByPostIdAndUserId(Long postId, Long userId);
    void deleteAllByPostId(Long postId);

    @Query(value = "SELECT board_type as boardType, post_id as id FROM (" +
            "   SELECT 'free' AS board_type, post_id, like_dt " +
            "   FROM free_board_like " +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 'share' AS board_type, post_id, like_dt " +
            "   FROM share_board_like " +
            "   WHERE user_id = :userId " +
            "   UNION ALL " +
            "   SELECT 'review' AS board_type, post_id, like_dt " +
            "   FROM review_board_like" +
            "   WHERE user_id = :userId " +
            "   UNION ALL " +
            "   SELECT 'recipe' AS board_type, post_id, like_dt " +
            "   FROM recipe_board_like " +
            "   WHERE user_id = :userId " +
            ") AS like_board " +
            "ORDER BY like_dt DESC " +
            "LIMIT :pageSize OFFSET :offset",
            nativeQuery = true)
    List<BoardLikeInterface> findAllLikeBoard(@Param("userId") Long userId, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Query(value = "SELECT count(*) FROM (" +
            "   SELECT 1 " +
            "   FROM free_board_like" +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 1 " +
            "   FROM share_board_like" +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 1 " +
            "   FROM review_board_like" +
            "   WHERE user_id = :userId " +
            "   UNION ALL" +
            "   SELECT 1 " +
            "   FROM recipe_board_like" +
            "   WHERE user_id = :userId " +
            ") AS count_like ", nativeQuery = true)
    int countLikeBoard(@Param("userId") Long userId);
}

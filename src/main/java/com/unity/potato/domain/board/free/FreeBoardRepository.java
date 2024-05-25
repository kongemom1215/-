package com.unity.potato.domain.board.free;

import com.unity.potato.domain.board.BoardInterface;
import com.unity.potato.domain.board.HotPostInterface;
import com.unity.potato.domain.vo.BoardVo;
import com.unity.potato.dto.BoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long>, FreeBoardExtension {

    Optional<FreeBoard> findByPostId(Long postId);
    Page<BoardVo> findAllByDeleteYnNot(char deleteYn, Pageable pageable);
    int countByWriterId(Long writerId);
    Page<BoardVo> findPopularByDeleteYnNot(char deleteYn, Pageable pageable);

    @Query(value = "SELECT board_type as boardType, post_id as id, writer_nickname as nickname, title, reg_dt as RegDt, read_cnt as readCnt, img_yn as imgYn, reply_cnt as replyCnt, region FROM (" +
            "    SELECT 'free' AS board_type, post_id, writer_nickname, title, reg_dt, read_cnt, img_yn, reply_cnt, '' as region, delete_yn " +
            "    FROM free_board " +
            "    WHERE writer_id = :writerId" +
            "    UNION ALL " +
            "    SELECT 'share' AS board_type, post_id, writer_nickname, title, reg_dt, read_cnt, img_yn, reply_cnt, '' as region, delete_yn " +
            "    FROM share_board " +
            "    WHERE writer_id = :writerId" +
            "    UNION ALL " +
            "    SELECT 'review' AS board_type, post_id, writer_nickname, title, reg_dt, read_cnt, img_yn, reply_cnt, region, delete_yn " +
            "    FROM review_board " +
            "    WHERE writer_id = :writerId" +
            "    UNION ALL " +
            "    SELECT 'recipe' AS board_type, post_id, writer_nickname, title, reg_dt, read_cnt, img_yn, reply_cnt, '' as region, delete_yn " +
            "    FROM recipe_board " +
            "    WHERE writer_id = :writerId" +
            ") AS board " +
            "WHERE delete_yn != 'Y'" +
            "ORDER BY reg_dt DESC " +
            "LIMIT :pageSize OFFSET :offset",
            nativeQuery = true)
    List<BoardInterface> findAllWriteBoard(@Param("writerId") Long writerId, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM ( " +
            "    SELECT 1 FROM free_board WHERE writer_id = :writerId AND delete_yn != 'Y' " +
            "    UNION ALL " +
            "    SELECT 1 FROM share_board WHERE writer_id = :writerId AND delete_yn != 'Y' " +
            "    UNION ALL " +
            "    SELECT 1 FROM review_board WHERE writer_id = :writerId AND delete_yn != 'Y' " +
            "    UNION ALL " +
            "    SELECT 1 FROM recipe_board WHERE writer_id = :writerId AND delete_yn != 'Y' " +
            ") AS boardCount", nativeQuery = true)
    int countWriteBoard(@Param("writerId") Long writerId);

    @Query(value = "SELECT boardType, post_id AS id, region, title, hotDegree " +
            "FROM (" +
            "   SELECT 'free' AS boardType, post_id, '' as region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM free_board" +
            "   WHERE DATE(reg_dt) = :previousDay" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'share' AS boardType, post_id, '' as region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM share_board" +
            "   WHERE DATE(reg_dt) = :previousDay" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'review' AS boardType, post_id, region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM review_board" +
            "   WHERE DATE(reg_dt) = :previousDay" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'recipe' AS boardType, post_id, '' AS region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM recipe_board" +
            "   WHERE DATE(reg_dt) = :previousDay" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   ) AS all_boards " +
            "ORDER BY hotDegree DESC LIMIT 20", nativeQuery = true)
    List<HotPostInterface> findTodayHotPosts(@Param("previousDay") String previousDay);

    @Query(value = "SELECT boardType, post_id AS id, region, title, hotDegree " +
            "FROM (" +
            "   SELECT 'free' AS boardType, post_id, '' as region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM free_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 7 DAY) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'share' AS boardType, post_id, '' as region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM share_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 7 DAY) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'review' AS boardType, post_id, region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM review_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 7 DAY) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'recipe' AS boardType, post_id, '' AS region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM recipe_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 7 DAY) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   ) AS all_boards " +
            "ORDER BY hotDegree DESC LIMIT 20", nativeQuery = true)
    List<HotPostInterface> findWeekHotPosts();

    @Query(value = "SELECT boardType, post_id AS id, region, title, hotDegree " +
            "FROM (" +
            "   SELECT 'free' AS boardType, post_id, '' as region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM free_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 1 MONTH) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'share' AS boardType, post_id, '' as region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM share_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 1 MONTH) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'review' AS boardType, post_id, region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM review_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 1 MONTH) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   UNION ALL" +
            "   SELECT 'recipe' AS boardType, post_id, '' AS region, title, (like_cnt + reply_cnt + read_cnt) AS hotDegree" +
            "   FROM recipe_board" +
            "   WHERE DATE(reg_dt) between DATE_SUB(CURDATE(), INTERVAL 1 MONTH) and DATE_SUB(CURDATE(), INTERVAL 1 DAY)" +
            "   AND delete_yn != 'Y'" +
            "   AND (like_cnt + read_cnt + reply_cnt) >= 50" +
            "   ) AS all_boards " +
            "ORDER BY hotDegree DESC LIMIT 20", nativeQuery = true)
    List<HotPostInterface> findMonthHotPosts();
}


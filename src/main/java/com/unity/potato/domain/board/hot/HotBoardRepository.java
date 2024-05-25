package com.unity.potato.domain.board.hot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotBoardRepository extends JpaRepository<HotBoard, Long> {
    @Query("SELECT h.hotDate FROM HotBoard h WHERE LENGTH(h.hotDate) = :length GROUP BY h.hotDate")
    List<String> findHotDates(@Param("length") int length);
    List<HotBoard> findTop4ByOrderByIdDesc();
}
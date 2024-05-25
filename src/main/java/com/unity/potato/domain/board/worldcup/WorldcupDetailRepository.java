package com.unity.potato.domain.board.worldcup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorldcupDetailRepository extends JpaRepository<WorldcupDetail, Long>, WorldcupDetailExtension {
    List<WorldcupDetail> findAllByWorldcupId(Long worldcupId);
    List<WorldcupDetail> findAllByWorldcupIdOrderByWinCntDesc(Long worldcupId);
    Optional<WorldcupDetail> findByIdAndWorldcupId(Long id, Long worldcupId);
}

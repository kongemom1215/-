package com.unity.potato.domain.board.worldcup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorldcupInfoRepository extends JpaRepository<WorldcupInfo, Long> {
    Optional<WorldcupInfo> findByVisibleYn(char visibleYn);
}

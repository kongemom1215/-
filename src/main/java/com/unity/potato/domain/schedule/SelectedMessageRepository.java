package com.unity.potato.domain.schedule;

import com.unity.potato.domain.vo.SelectedMessageVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SelectedMessageRepository extends JpaRepository<SelectedMessage, Long> {
    @Modifying
    @Query(value = "TRUNCATE TABLE selected_message", nativeQuery = true)
    void truncateTable();
}

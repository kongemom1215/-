package com.unity.potato.domain.schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectMessageRepository extends JpaRepository<CollectMessage, Long> {
    int countBySelected(boolean selected);

    Page<CollectMessage> findAllBySelected(boolean selected, PageRequest request);
}

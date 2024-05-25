package com.unity.potato.domain.map;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MapListRepository extends JpaRepository<MapList, Long> {
    List<MapList> findAllByViewYn(char viewYn);
}

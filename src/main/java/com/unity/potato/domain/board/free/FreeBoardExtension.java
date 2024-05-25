package com.unity.potato.domain.board.free;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FreeBoardExtension {
    Page<FreeBoard> searchByTitle(String keyword, Pageable pageable);
    Page<FreeBoard> searchByContent(String keyword, Pageable pageable);
    Page<FreeBoard> searchByTitleAndContent(String keyword, Pageable pageable);
    long countSearch(String keyword);
    List<FreeBoard> searchBoardLimit3(String keyword);
}

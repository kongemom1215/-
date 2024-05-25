package com.unity.potato.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardPageDTO {
    private List<BoardDTO> boardDTOList;
    private int pageNumber;
    private boolean hasNext;
}

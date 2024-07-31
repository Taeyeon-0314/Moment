package com.mo.moment.dto.boardDto;

import lombok.*;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailViewDto {
    private Long boardId;
    private String content;
    private List<BoardImageViewDto> accessUrl;
}

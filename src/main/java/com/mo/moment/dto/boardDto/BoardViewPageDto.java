package com.mo.moment.dto.boardDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardViewPageDto {
    private List<BoardPageRequestDto> board;
    private int pageSize;
    private int pageNumber;
    private int totalPages;
    private boolean last;
}

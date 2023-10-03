package com.mo.moment.dto.boardDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardResponseDto {
    private String content;

    @Builder
    private BoardResponseDto (String content){
        this.content = content;
    }
}

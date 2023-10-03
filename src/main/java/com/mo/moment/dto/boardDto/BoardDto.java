package com.mo.moment.dto.boardDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class BoardDto {
    private Long board_id;
    private String content;
    private Date datetime;

    @Builder
    private BoardDto (Long board_id , String content){
        this.board_id = board_id;
        this.content = content;
    }

}

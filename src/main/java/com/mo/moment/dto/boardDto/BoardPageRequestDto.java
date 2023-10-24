package com.mo.moment.dto.boardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardPageRequestDto {
    private Long board_id;
    private LocalDateTime dateTime;
    private String content;
}

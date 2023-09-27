package com.mo.moment.dto.postDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class PostDto {
    private Long board_id;
    private Long kakao_id;
    private String content;
    private Date datetime;


}

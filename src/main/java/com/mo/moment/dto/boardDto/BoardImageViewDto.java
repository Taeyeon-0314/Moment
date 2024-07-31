package com.mo.moment.dto.boardDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardImageViewDto {
    private Long imageId;
    private String originName;
    private Date metaDateTime;
    private String accessUrl;
}

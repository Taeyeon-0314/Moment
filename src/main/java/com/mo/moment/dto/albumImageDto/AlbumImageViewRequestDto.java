package com.mo.moment.dto.albumImageDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumImageViewRequestDto {

    private Long imageId;
    private String originName;
    private String accessUrl;
    private String resizeUrl;
    private Date metaDateTime;
    private Long boardId;
    private boolean contentCheck;




}

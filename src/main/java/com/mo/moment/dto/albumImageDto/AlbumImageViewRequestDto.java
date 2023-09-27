package com.mo.moment.dto.albumImageDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor
public class AlbumImageViewRequestDto {

    private Long id;
    private String accessUrl;
    private String resizeUrl;
    private Date metaDateTime;
    private Long boardId;



    @Builder
    public AlbumImageViewRequestDto(Long id , String resizeUrl , String accessUrl ,  Date metaDateTime , Long boardId ){
        this.id = id;
        this.resizeUrl = resizeUrl;
        this.accessUrl = accessUrl;
        this.metaDateTime = metaDateTime;
        this.boardId = boardId;
    }
}

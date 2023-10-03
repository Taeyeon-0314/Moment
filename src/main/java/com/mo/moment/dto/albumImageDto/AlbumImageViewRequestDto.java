package com.mo.moment.dto.albumImageDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
@Getter
@NoArgsConstructor
public class AlbumImageViewRequestDto {

    private Long id;
    private String originName;
    private String accessUrl;
    private String resizeUrl;
    private Date metaDateTime;
    private Long boardId;
    private boolean contentCheck;



    @Builder
    public AlbumImageViewRequestDto(Long id , String resizeUrl , String accessUrl , String originName ,  Date metaDateTime , Long boardId , boolean contentCheck){
        this.id = id;
        this.originName = originName;
        this.accessUrl = accessUrl;
        this.resizeUrl = resizeUrl;
        this.metaDateTime = metaDateTime;
        this.boardId = boardId;
        this.contentCheck = contentCheck;
    }
}

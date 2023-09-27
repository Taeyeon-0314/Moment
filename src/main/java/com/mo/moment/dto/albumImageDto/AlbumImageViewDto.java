package com.mo.moment.dto.albumImageDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class AlbumImageViewDto {

    private Long id;

    private String storedName;

    private String accessUrl;

    private Date metaDateTime;

    private Long kakaoId;
    private Long boardId;

    @Builder
    public AlbumImageViewDto(Long id , String storedName , String accessUrl ,  Date metaDateTime ,  Long boardId , Long kakaoId){
        this.id = id;
        this.storedName = storedName;
        this.accessUrl = accessUrl;
        this.metaDateTime = metaDateTime;
        this.boardId = boardId;
        this.kakaoId = kakaoId;
    }

}

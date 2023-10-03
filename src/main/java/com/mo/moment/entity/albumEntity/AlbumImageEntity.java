package com.mo.moment.entity.albumEntity;

import com.mo.moment.entity.boardEntity.BoardEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "album_image")
public class AlbumImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;
    @Column(name = "origin_name" , length = 100)
    private String originName; // 이미지 파일의 본래 이름
    @Column(name = "stored_name" , length = 200)
    private String storedName; // 이미지 파일이 S3에 저장될때 사용되는 이름
    @Column(name = "access_url" , length = 200)
    private String accessUrl; // S3 내부 이미지에 접근할 수 있는 URL
    @Column(name = "meta_date_time")
    private Date metaDateTime;
    @Column(name = "resize_url" , length = 200) // S3 리사이징 이미지에 접근할 수 있는 URL
    private String resizeUrl;
    @Column(name = "kakao_id")
    private Long kakaoId;
    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardEntity boardEntity;



    public AlbumImageEntity(String originName) {
        this.originName = originName;
        this.storedName = getFileName(originName);
        this.accessUrl = "";
        this.resizeUrl = "";
    }


    public void setMetaDateTime(Date metaDateTime){
        this.metaDateTime = metaDateTime;
    }
    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }
    public void setResizeUrl (String resizeUrl) {
        this.resizeUrl = resizeUrl;
    }

    public void setBoardEntity(BoardEntity boardEntity) {
        this.boardEntity = boardEntity;
    }

    // 이미지 파일의 확장자를 추출하는 메소드
    public String extractExtension(String originName) {
        int index = originName.lastIndexOf('.');

        return originName.substring(index, originName.length());
    }

    // 이미지 파일의 이름을 저장하기 위한 이름으로 변환하는 메소드
    public String getFileName(String originName) {
        return UUID.randomUUID() + "." + extractExtension(originName);
    }

}

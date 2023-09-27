package com.mo.moment.entity.albumEntity;

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

    private String originName; // 이미지 파일의 본래 이름

    private String storedName; // 이미지 파일이 S3에 저장될때 사용되는 이름

    private String accessUrl; // S3 내부 이미지에 접근할 수 있는 URL

    private Date metaDateTime;

    private String resizeUrl;

    private Long kakaoId;
    private Long boardId;



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

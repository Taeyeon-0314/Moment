package com.mo.moment.entity.kakaoEntity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Entity
@Table(name = "kakao_login")
public class KakaoLoginEntity {
    @Id
    private Long kakaoId;
    @Column(unique = true)
    private String email;
    @Column(name = "kakao_name")
    private String kakaoName;
    @Column(name = "profile_image")
    private String profile_image;

    public KakaoLoginEntity() {
    }

    public KakaoLoginEntity(String email, String kakaoName , String profile_image) {
        this.email = email;
        this.kakaoName = kakaoName;
        this.profile_image = profile_image;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setKakaoName(String kakaoName) {
        this.kakaoName = kakaoName;
    }

    public void setProfile_image(String profileImage){
        this.profile_image = profileImage;
    }

    @Builder
    public KakaoLoginEntity(Long kakaoId, String email, String kakaoName, String profile_image) {
        this.kakaoId=kakaoId;
        this.email = email;
        this.kakaoName = kakaoName;
        this.profile_image = profile_image;
    }
}

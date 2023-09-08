package com.mo.moment.entity.kakaoEntity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "kakao_login_entity")
public class KakaoLoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long kakaoId;
    private String email;
    @Column(name = "kakao_name")
    private String kakaoName;
    @Column(name = "profile_image")
    private String profile_image;

    public KakaoLoginEntity() {
    }

    public KakaoLoginEntity(Long kakaoId,String email, String kakaoName , String profile_image) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.kakaoName = kakaoName;
        this.profile_image = profile_image;
    }

    public void setId(Long id) {
        this.id = id;
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
    public KakaoLoginEntity(Long id, Long kakaoId, String email, String kakaoName, String profile_image) {
        this.id = id;
        this.kakaoId=kakaoId;
        this.email = email;
        this.kakaoName = kakaoName;
        this.profile_image = profile_image;
    }
}

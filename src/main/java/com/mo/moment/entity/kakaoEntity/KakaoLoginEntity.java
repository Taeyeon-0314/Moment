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
    private String email;
    @Column(name = "kakao_name")
    private String kakaoName;

    public KakaoLoginEntity() {
    }

    public KakaoLoginEntity(String email, String kakaoName) {
        this.email = email;
        this.kakaoName = kakaoName;
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

    @Builder
    public KakaoLoginEntity(Long id, String email, String kakaoName) {
        this.id = id;
        this.email = email;
        this.kakaoName = kakaoName;
    }
}

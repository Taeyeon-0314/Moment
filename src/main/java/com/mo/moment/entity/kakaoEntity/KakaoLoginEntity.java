package com.mo.moment.entity.kakaoEntity;

import com.mo.moment.entity.boardEntity.BoardEntity;
import com.mo.moment.jwt.entity.RefreshToken;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "kakao_login")
public class KakaoLoginEntity {
    @Id
    @Column(name = "kakao_id")
    private Long kakaoId;
    @Column(unique = true)
    private String email;
    @Column(name = "kakao_name")
    private String kakaoName;
    @Column(name = "profile_image")
    private String profile_image;
    @OneToMany(mappedBy = "kakaoLoginEntity" , cascade = CascadeType.REMOVE , orphanRemoval = true , fetch = FetchType.LAZY)
    private List<BoardEntity> boardEntities = new ArrayList<>();
    @OneToOne(mappedBy = "kakaoLoginEntity" , cascade = CascadeType.REMOVE , orphanRemoval = true , fetch = FetchType.LAZY)
    private RefreshToken refreshToken;

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

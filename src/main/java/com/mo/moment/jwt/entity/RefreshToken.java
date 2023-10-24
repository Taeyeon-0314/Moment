package com.mo.moment.jwt.entity;

import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "kakao_id")
    private KakaoLoginEntity kakaoLoginEntity;

    @Column(nullable = false)
    private String token;

    public RefreshToken updateToken(String token) {
        this.token = token;
        return this;
    }


}


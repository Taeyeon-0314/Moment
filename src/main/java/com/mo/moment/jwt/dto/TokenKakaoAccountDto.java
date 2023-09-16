package com.mo.moment.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TokenKakaoAccountDto {
    String accessToken;
    String refreshToken;
    Date accessTokenExpireDate;
    Long kakaoId;
    String profile_image;
    String kakaoName;
    String email;


    @Builder
    public TokenKakaoAccountDto(String accessToken,String refreshToken, Date accessTokenExpireDate, Long kakaoId,
                                String email, String kakaoName, String profile_image) {
        this.kakaoId=kakaoId;
        this.email = email;
        this.kakaoName = kakaoName;
        this.profile_image = profile_image;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpireDate = accessTokenExpireDate;
    }

}

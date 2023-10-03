package com.mo.moment.jwt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TokenKakaoAccountDto {
    private Long kakaoId;
    private String kakaoName;
    private String email;
    private String profile_image;
    private String accessToken;
    private String refreshToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Date accessTokenExpireDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Date issuedAt;



    @Builder
    public TokenKakaoAccountDto(String accessToken, String refreshToken, Date accessTokenExpireDate, Long kakaoId,
                                String email, String kakaoName, String profile_image, Date issuedAt) {
        this.kakaoId = kakaoId;
        this.email = email;
        this.kakaoName = kakaoName;
        this.profile_image = profile_image;
        this.accessToken = accessToken;
        this.issuedAt = issuedAt;
        this.refreshToken = refreshToken;
        this.accessTokenExpireDate = accessTokenExpireDate;
    }

}

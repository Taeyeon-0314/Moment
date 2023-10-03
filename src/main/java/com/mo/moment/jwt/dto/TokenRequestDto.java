package com.mo.moment.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TokenRequestDto {
    private String accessToken;
    private String refreshToken;
    private Date issuedAt;
    private Date accessTokenExpireDate;

    @Builder
    public TokenRequestDto(String accessToken, String refreshToken, Date accessTokenExpireDate , Date issuedAt){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.issuedAt = issuedAt;
        this.accessTokenExpireDate = accessTokenExpireDate;
    }
}

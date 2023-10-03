package com.mo.moment.dto.kakaoDto;

import lombok.Builder;
import lombok.Data;

@Data
public class KakaoTokenSaveDto {
    private String access_token;
    private String token_type;
    private String refresh_token;
    private String id_token;
    private int expires_in;
    private int refresh_token_expires_in;
    private String scope;

    @Builder
    private KakaoTokenSaveDto (String access_token, String token_type , String refresh_token , String id_token , int expires_in , int refresh_token_expires_in , String scope){
        this.access_token = access_token;
        this.token_type = token_type;
        this.refresh_token = refresh_token;
        this.id_token = id_token;
        this.expires_in = expires_in;
        this.refresh_token_expires_in = refresh_token_expires_in;
        this.scope = scope;
    }
}

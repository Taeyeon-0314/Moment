package com.mo.moment.dto.kakaoDto;

import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import com.mo.moment.jwt.AuthToken;
import lombok.Data;

@Data
public class LoginResponseDto {
    public boolean loginSuccess;
    public KakaoLoginEntity kakaoLoginEntity;
    public AuthToken authToken;
}

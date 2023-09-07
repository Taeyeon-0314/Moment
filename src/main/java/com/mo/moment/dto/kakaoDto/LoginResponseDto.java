package com.mo.moment.dto.kakaoDto;

import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import lombok.Data;

@Data
public class LoginResponseDto {
    public boolean loginSuccess;
    public KakaoLoginEntity kakaoLoginEntity;
}

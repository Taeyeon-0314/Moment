package com.mo.moment.jwt.dto;

import com.mo.moment.dto.kakaoDto.ReissueKakaoUserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenReissueKakaoUserInfoDto {

    private ReissueKakaoUserInfoDto kakaoUserInfoDto;
    private Token token;
}

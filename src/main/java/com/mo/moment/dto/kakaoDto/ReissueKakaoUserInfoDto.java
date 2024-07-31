package com.mo.moment.dto.kakaoDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReissueKakaoUserInfoDto {
    private Long kakaoId;
    private String email;
    private String kakaoName;
    private String profile_image;
}

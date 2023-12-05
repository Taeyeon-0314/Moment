package com.mo.moment.dto.myPageDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageMainResponseDto {
    private Long kakaoId;
    private String kakaoName;
    private String profileImage;
    private Long messageCount;
    private Long imageCount;
}

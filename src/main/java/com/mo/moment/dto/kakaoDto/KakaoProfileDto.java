package com.mo.moment.dto.kakaoDto;

import lombok.Data;

@Data
public class KakaoProfileDto {
    private String nickname;

    public KakaoProfileDto() {
    }

    public KakaoProfileDto(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

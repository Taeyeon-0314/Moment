package com.mo.moment.dto.kakaoDto;

import lombok.Data;

@Data
public class KakaoProfileDto {
    private String nickname;
    private String profile_image_url;

    public KakaoProfileDto() {
    }

    public KakaoProfileDto(String nickname , String profile_image_url) {
        this.nickname = nickname;
        this.profile_image_url = profile_image_url;
    }

    public String getNickname() {
        return nickname;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }
}

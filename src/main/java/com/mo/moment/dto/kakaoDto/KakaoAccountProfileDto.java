package com.mo.moment.dto.kakaoDto;

import lombok.Data;

@Data
public class KakaoAccountProfileDto {
    private String email;
    private KakaoProfileDto profile;

    public KakaoAccountProfileDto() {
    }

    public KakaoAccountProfileDto(String email, KakaoProfileDto profile) {
        this.email = email;
        this.profile = profile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public KakaoProfileDto getProfile() {
        return profile;
    }

    public void setProfile(KakaoProfileDto profile) {
        this.profile = profile;
    }
}

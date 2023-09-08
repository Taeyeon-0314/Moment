package com.mo.moment.controller;

import com.mo.moment.dto.kakaoDto.KakaoTokenDto;
import com.mo.moment.dto.kakaoDto.LoginResponseDto;
import com.mo.moment.service.kakaoService.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;
    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(HttpServletRequest request){
        String code = request.getParameter("code");
        KakaoTokenDto kakaoAccessToken = kakaoLoginService.getKakaoAccessToken(code);
        return kakaoLoginService.kakaoLogin(kakaoAccessToken.getAccess_token());
    }
}

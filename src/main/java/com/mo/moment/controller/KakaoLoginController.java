package com.mo.moment.controller;

import com.mo.moment.dto.kakaoDto.KakaoTokenDto;
import com.mo.moment.dto.kakaoDto.LoginResponseDto;
import com.mo.moment.service.kakaoService.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/login")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam("code") String code){
        KakaoTokenDto kakaoAccessToken = kakaoLoginService.getKakaoAccessToken(code);
        ResponseEntity<LoginResponseDto> loginResponseDtoResponseEntity = kakaoLoginService.kakaoLogin(kakaoAccessToken.getAccess_token());
        return loginResponseDtoResponseEntity;
    }
}

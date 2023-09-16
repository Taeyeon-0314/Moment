package com.mo.moment.controller;

import com.mo.moment.dto.kakaoDto.KakaoTokenDto;
import com.mo.moment.jwt.dto.Token;
import com.mo.moment.jwt.dto.TokenKakaoAccountDto;
import com.mo.moment.jwt.dto.TokenRequestDto;
import com.mo.moment.response.ResponseService;
import com.mo.moment.response.SingleResult;
import com.mo.moment.service.kakaoService.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;
    private final ResponseService responseService;
    @GetMapping("/login")
    public ResponseEntity<TokenKakaoAccountDto> kakaoLogin(@RequestParam("code") String code , @RequestParam(value = "text" , required = false) String text ){
        if(text == null) {
            KakaoTokenDto kakaoAccessToken = kakaoLoginService.getKakaoAccessToken(code);
            return kakaoLoginService.kakaoLogin(kakaoAccessToken.getAccess_token());
        }else{
            KakaoTokenDto kakaoAccessToken = kakaoLoginService.getKakaoAccessToken1(code,text);
            return kakaoLoginService.kakaoLogin(kakaoAccessToken.getAccess_token());
        }
    }

    // 토큰을 재발급
    @PostMapping("/reissue")
    public SingleResult<Token> reissue(@RequestBody TokenRequestDto tokenRequestDto){
        // 토큰 재발급 하고 결과 반환
        return responseService.getSingleResult(kakaoLoginService.reissue(tokenRequestDto));
    }

}

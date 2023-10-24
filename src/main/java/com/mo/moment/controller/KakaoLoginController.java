package com.mo.moment.controller;

import com.mo.moment.config.KoreaTime;
import com.mo.moment.dto.kakaoDto.KakaoTokenDto;
import com.mo.moment.dto.kakaoDto.KakaoTokenSaveDto;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.jwt.dto.TokenKakaoAccountDto;
import com.mo.moment.jwt.dto.TokenRequestDto;
import com.mo.moment.response.ResponseService;
import com.mo.moment.service.kakaoService.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
@Log4j2
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;
    private final ResponseService responseService;
    private final AuthTokenProvider authTokenProvider;

    @GetMapping("/login")
    public ResponseEntity<TokenKakaoAccountDto> kakaoLogin(@RequestParam("code") String code) {
        KakaoTokenDto kakaoAccessToken = kakaoLoginService.getKakaoAccessToken(code);

        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("[kakaoLogin]");
        log.info("KST Date: {}, kakaoAccessToken: {}", zonedDateTime, kakaoAccessToken);
        return kakaoLoginService.kakaoLogin(kakaoAccessToken);
    }

    // 토큰을 재발급
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody TokenRequestDto tokenRequestDto, HttpServletRequest request) {
        String header = request.getHeader("X-AUTH-TOKEN");
        KakaoTokenSaveDto kakaoToken = authTokenProvider.getKakaoToken(header);
        // 토큰 재발급 하고 결과 반환
        return kakaoLoginService.reissue(tokenRequestDto, kakaoToken);
    }

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<TokenKakaoAccountDto> kakaoLogin(HttpServletRequest request) {
        String code = request.getParameter("code");
        KakaoTokenDto kakaoAccessToken = kakaoLoginService.getKakaoAccessToken1(code);

        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("[kakaoLogin]");
        log.info("KST Date: {}, kakaoAccessToken: {}", zonedDateTime, kakaoAccessToken);

        return kakaoLoginService.kakaoLogin(kakaoAccessToken);
    }

}

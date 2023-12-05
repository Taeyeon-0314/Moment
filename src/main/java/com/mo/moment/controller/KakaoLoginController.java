package com.mo.moment.controller;

import com.mo.moment.config.KoreaTime;
import com.mo.moment.dto.kakaoDto.KakaoTokenDto;
import com.mo.moment.jwt.dto.TokenKakaoAccountDto;
import com.mo.moment.service.kakaoService.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
@Log4j2
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final KakaoLoginService kakaoLoginService;

    @GetMapping("/login")
    public ResponseEntity<TokenKakaoAccountDto> kakaoLogin(@RequestParam("code") String code) {
        KakaoTokenDto kakaoAccessToken = kakaoLoginService.getKakaoAccessToken(code);
        log.info("[kakaoLogin]");
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("KST Date: {}, kakaoAccessToken: {}", zonedDateTime, kakaoAccessToken);
        return kakaoLoginService.kakaoLogin(kakaoAccessToken);
    }

    // 토큰을 재발급
    @GetMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request) {
        log.info("[kakaoReissue]");
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    // "refreshToken"이라는 이름의 쿠키를 찾음
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        // 토큰 재발급 하고 결과 반환
        return kakaoLoginService.reissue(refreshToken);
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

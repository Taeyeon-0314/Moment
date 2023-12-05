package com.mo.moment.service.kakaoService;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mo.moment.config.KoreaTime;
import com.mo.moment.dto.kakaoDto.*;
import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.jwt.dto.Token;
import com.mo.moment.jwt.dto.TokenKakaoAccountDto;
import com.mo.moment.jwt.dto.TokenReissueKakaoUserInfoDto;
import com.mo.moment.jwt.dto.TokenRequestDto;
import com.mo.moment.jwt.entity.RefreshToken;
import com.mo.moment.jwt.exception.CRefreshTokenException;
import com.mo.moment.jwt.exception.CUserNotFoundException;
import com.mo.moment.repository.jwtRepository.JwtRepository;
import com.mo.moment.repository.kakaoRepository.KakaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class KakaoLoginService {

    private final KakaoRepository kakaoRepository;
    private final AuthTokenProvider authTokenProvider;
    private final JwtRepository jwtRepository;


    @Value("${kakao.auth.clientId}")
    private String clientId;
    @Value("${kakao.auth.clientSecret}")
    private String clientSecret;

    // 카카오 Oauth2를 통해 액세스 토큰을 가져오는 메서드
    @Transactional
    public KakaoTokenDto getKakaoAccessToken(String code) {


        // Http 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 매개변수 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "https://www.moment.r-e.kr/login/oauth2/callback/kakao");
//        params.add("redirect_uri", "http://localhost:3000/login/oauth2/callback/kakao");
        params.add("code", code);
        params.add("client_secret", clientSecret);

        // HTTP 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // RestTemplate을 사용하여 카카오 서버에 액세스 토큰 요청을 보내고 응답을 받음
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON 응답 데이터를 KakaoTokenDto 객체로 역직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return kakaoTokenDto;
    }

    //서버 로컬 테스트용
    @Transactional
    public KakaoTokenDto getKakaoAccessToken1(String code) {


        // Http 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 매개변수 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:8080/login/oauth2/code/kakao");
        params.add("code", code);
        params.add("client_secret", clientSecret);

        // HTTP 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // RestTemplate을 사용하여 카카오 서버에 액세스 토큰 요청을 보내고 응답을 받음
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON 응답 데이터를 KakaoTokenDto 객체로 역직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return kakaoTokenDto;
    }

    // 카카오 액세스 토큰을 사용하여 로그인 처리를 수행하는 메소드
    public ResponseEntity<TokenKakaoAccountDto> kakaoLogin(KakaoTokenDto kakaoAccessToken) {

        // 카카오 사용자 정보 조회
        KakaoLoginEntity kakaoLoginEntity = getKakaoInfo(kakaoAccessToken.getAccess_token());

        // 로그인 응답 데이터 생성
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setLoginSuccess(true);
        loginResponseDto.setKakaoLoginEntity(kakaoLoginEntity);

        // 데이터베이스에서 해당 카카오 사용자 정보 조회
        KakaoLoginEntity existOwner = kakaoRepository.findById(kakaoLoginEntity.getKakaoId()).orElse(null);

        // jwt토큰 발급
        ReissueKakaoUserInfoDto reissueKakaoUserInfoDto = ReissueKakaoUserInfoDto.builder()
                .kakaoId(kakaoLoginEntity.getKakaoId())
                .profile_image(kakaoLoginEntity.getProfile_image())
                .kakaoName(kakaoLoginEntity.getKakaoName())
                .email(kakaoLoginEntity.getEmail())
                .build();
        Token token = authTokenProvider.createToken(kakaoLoginEntity.getKakaoId(), reissueKakaoUserInfoDto);


        try {
            if (existOwner == null) {
                kakaoRepository.save(kakaoLoginEntity);
                // 데이터베이스에 새로운 회원으로 저장
                RefreshToken refreshToken = RefreshToken.builder()
                        .token(token.getRefreshToken())
                        .kakaoLoginEntity(kakaoLoginEntity)
                        .build();
                jwtRepository.save(refreshToken);

            }
            loginResponseDto.setLoginSuccess(true);

            Long kakaoId = kakaoLoginEntity.getKakaoId();
            Optional<RefreshToken> optionalRefreshToken = jwtRepository.findByKakaoLoginEntity_KakaoId(kakaoId);
            if (optionalRefreshToken.isEmpty()) {
                RefreshToken refreshToken = RefreshToken.builder()
                        .token(token.getRefreshToken())
                        .kakaoLoginEntity(kakaoLoginEntity)
                        .build();
                Long id = jwtRepository.save(refreshToken).getId();
                RefreshToken refreshToken1 = jwtRepository.findById(id).get();
                TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                        .accessToken(token.getAccessToken())
                        .issuedAt(token.getIssuedAt())
                        .refreshToken(token.getRefreshToken())
                        .accessTokenExpireDate(token.getAccessTokenExpireDate())
                        .build();

                TokenKakaoAccountDto tokenKakaoAccountDto = TokenKakaoAccountDto.builder()
                        .accessToken(tokenRequestDto.getAccessToken())
                        .accessTokenExpireDate(tokenRequestDto.getAccessTokenExpireDate())
                        .kakaoId(refreshToken1.getKakaoLoginEntity().getKakaoId())
                        .profile_image(existOwner.getProfile_image())
                        .issuedAt(tokenRequestDto.getIssuedAt())
                        .kakaoName(existOwner.getKakaoName())
                        .refreshToken(token.getRefreshToken())
                        .email(kakaoLoginEntity.getEmail())
                        .build();
                String refreshTokenCookie = token.getRefreshToken();

                ResponseCookie cookie = ResponseCookie.from("refreshToken" , refreshTokenCookie)
                        .maxAge(7*24 * 60 * 60)
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .build();

                KoreaTime koreaTime = new KoreaTime();
                ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
                log.info("KST Date: {}, tokenKakaoAccountDto: {}", zonedDateTime, tokenKakaoAccountDto);
                // 로그인 성공 응답 반환
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(tokenKakaoAccountDto);

            } else {
                RefreshToken refreshToken = optionalRefreshToken.get();
                TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                        .accessToken(token.getAccessToken())
                        .issuedAt(token.getIssuedAt())
                        .refreshToken(token.getRefreshToken())
                        .accessTokenExpireDate(token.getAccessTokenExpireDate())
                        .build();

                RefreshToken refreshToken1 = RefreshToken.builder()
                        .id(refreshToken.getId())
                        .token(token.getRefreshToken())
                        .kakaoLoginEntity(refreshToken.getKakaoLoginEntity())
                        .build();
                jwtRepository.save(refreshToken1);
                TokenKakaoAccountDto tokenKakaoAccountDto = TokenKakaoAccountDto.builder()
                        .accessToken(tokenRequestDto.getAccessToken())
                        .accessTokenExpireDate(tokenRequestDto.getAccessTokenExpireDate())
                        .kakaoId(refreshToken.getKakaoLoginEntity().getKakaoId())
                        .profile_image(existOwner.getProfile_image())
                        .issuedAt(tokenRequestDto.getIssuedAt())
                        .kakaoName(existOwner.getKakaoName())
                        .refreshToken(token.getRefreshToken())
                        .email(kakaoLoginEntity.getEmail())
                        .build();
                String refreshTokenCookie = token.getRefreshToken();
                ResponseCookie cookie = ResponseCookie.from("refreshToken" , refreshTokenCookie)
                        .maxAge(7*24 * 60 * 60)
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .build();

                KoreaTime koreaTime = new KoreaTime();
                ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
                log.info("KST Date: {}, tokenKakaoAccountDto: {}", zonedDateTime, tokenKakaoAccountDto);
                // 로그인 성공 응답 반환
                return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,cookie.toString()).body(tokenKakaoAccountDto);
            }

        } catch (Exception e) {
            // 로그인 실패 응답 반환
            loginResponseDto.setLoginSuccess(false);
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 카카오 액세스 토큰을 사용하여 카카오 사용자 정보를 조회하는 메소드
    public KakaoLoginEntity getKakaoInfo(String kakaoAccessToken) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        // POST 방식으로 API 서버에 요청 후 response 받아옴
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        // JSON Parsing (-> kakaoAccountDto)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoAccountDto kakaoAccountDto = null;
        try {

            kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // 회원가입 처리하기
        Long kakaoId = kakaoAccountDto.getId();
        KakaoLoginEntity existOwner = kakaoRepository.findById(kakaoId).orElse(null);

        // 처음 로그인이 아닌 경우
        if (existOwner != null) {
            return KakaoLoginEntity.builder()
                    .kakaoId(kakaoAccountDto.getId())
                    .email(kakaoAccountDto.getKakao_account().getEmail())
                    .kakaoName(kakaoAccountDto.getKakao_account().getProfile().getNickname())
                    .profile_image(kakaoAccountDto.getKakao_account().getProfile().getProfile_image_url())
                    .build();
        }

        // 처음 로그인 하는 경우
        else {
            return KakaoLoginEntity.builder()
                    .kakaoId(kakaoAccountDto.getId())
                    .email(kakaoAccountDto.getKakao_account().getEmail())
                    .kakaoName(kakaoAccountDto.getKakao_account().getProfile().getNickname())
                    .profile_image(kakaoAccountDto.getKakao_account().getProfile().getProfile_image_url())
                    .build();
        }
    }

    // 토큰 재발급
    @Transactional
    public ResponseEntity<?> reissue(String refreshTokenReissue) {

        // 리프레시 토큰이 유효하지 않으면 예외발생
        if (!authTokenProvider.validationToken(refreshTokenReissue)) {
            throw new CRefreshTokenException();
        }

        //인증된 사용자의 정보를 데이터베이스에서 가져옴 / 찾을수 없으면 예외
        KakaoLoginEntity kakaoLoginEntity = kakaoRepository.findById(jwtRepository.findByToken(refreshTokenReissue).getKakaoLoginEntity().getKakaoId())
                .orElseThrow(CUserNotFoundException::new);

        //리프레시 토큰을 데이터베이스에서 가져옴 / 찾을수 없으면 예외
        RefreshToken refreshToken = jwtRepository.findByKakaoLoginEntity_KakaoId(kakaoLoginEntity.getKakaoId())
                .orElseThrow(CRefreshTokenException::new);

        // 요청받은 리프레시 토큰과 데이터베이스의 리프레시 토큰이 일치하지 않으면 예외 발생
        if (!refreshToken.getToken().equals(refreshTokenReissue)) {
            throw new CRefreshTokenException();
        }

        ReissueKakaoUserInfoDto reissueKakaoUserInfoDto = ReissueKakaoUserInfoDto.builder()
                .kakaoId(kakaoLoginEntity.getKakaoId())
                .profile_image(kakaoLoginEntity.getProfile_image())
                .kakaoName(kakaoLoginEntity.getKakaoName())
                .email(kakaoLoginEntity.getEmail())
                .build();

        // 새로운 토큰 생성
        Token newCreatedToken = authTokenProvider.createToken(kakaoLoginEntity.getKakaoId(), reissueKakaoUserInfoDto);

        // 리프레시 토큰을 업데이트하고 db저장
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        jwtRepository.save(updateRefreshToken);
        KakaoLoginEntity kakaoLoginEntity1 = kakaoRepository.findById(kakaoLoginEntity.getKakaoId()).get();
        ReissueKakaoUserInfoDto reissueKakaoUserInfoDto1 = ReissueKakaoUserInfoDto.builder()
                .kakaoId(kakaoLoginEntity1.getKakaoId())
                .email(kakaoLoginEntity1.getEmail())
                .kakaoName(kakaoLoginEntity1.getKakaoName())
                .profile_image(kakaoLoginEntity1.getProfile_image())
                .build();
        TokenReissueKakaoUserInfoDto tokenReissueKakaoUserInfoDto = TokenReissueKakaoUserInfoDto.builder()
                .token(newCreatedToken)
                .kakaoUserInfoDto(reissueKakaoUserInfoDto1)
                .build();
        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if (currentAuthentication != null) {
            // 만료시킬 인증 정보를 생성합니다. 예를 들어, 인증 토큰을 비워서 만료시킬 수 있습니다.
            Authentication expiredAuthentication = new UsernamePasswordAuthenticationToken(null, null, null);
            // SecurityContextHolder에 새로운 인증 정보를 설정하여 현재 인증 정보를 만료시킵니다.
            SecurityContextHolder.getContext().setAuthentication(expiredAuthentication);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken" , newCreatedToken.getRefreshToken())
                .maxAge(7*24 * 60 * 60)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();


        // 새로 생성된 토큰 반환
        return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE,cookie.toString()).body(tokenReissueKakaoUserInfoDto);
    }

}

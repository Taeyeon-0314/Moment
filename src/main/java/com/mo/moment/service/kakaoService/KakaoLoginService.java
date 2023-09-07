package com.mo.moment.service.kakaoService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mo.moment.dto.kakaoDto.KakaoAccountDto;
import com.mo.moment.dto.kakaoDto.KakaoTokenDto;
import com.mo.moment.dto.kakaoDto.LoginResponseDto;
import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import com.mo.moment.repository.kakaoRepository.KakaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoRepository kakaoRepository;

    @Transactional
    public KakaoTokenDto getKakaoAccessToken(String code){

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type" , "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "38f086bf0a00724a17a65858229e9333");
        params.add("redirect_uri","http://localhost:8080/login/oauth2/callback/kakao");
        params.add("code",code);
        params.add("client_secret","Lzak9SeSUnexiSOSxcOdgf90kKU0oGzo");

        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(params,headers);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> accessTokenResponse  = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        KakaoTokenDto kakaoTokenDto = null;
        try{
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(),KakaoTokenDto.class);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return kakaoTokenDto;
    }

    public ResponseEntity<LoginResponseDto> kakaoLogin(String kakaoAccessToken) {
        KakaoLoginEntity kakaoLoginEntity = getKakaoInfo(kakaoAccessToken);
        HttpHeaders headers = new HttpHeaders();

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setLoginSuccess(true);
        loginResponseDto.setKakaoLoginEntity(kakaoLoginEntity);

        KakaoLoginEntity existOwner = kakaoRepository.findById(kakaoLoginEntity.getId()).orElse(null);
        try {
            if (existOwner == null) {
                System.out.println("처음 로그인 하는 회원입니다.");
                kakaoRepository.save(kakaoLoginEntity);
            }
            loginResponseDto.setLoginSuccess(true);

            return ResponseEntity.ok().headers(headers).body(loginResponseDto);

        } catch (Exception e) {
            loginResponseDto.setLoginSuccess(false);
            return ResponseEntity.badRequest().body(loginResponseDto);
        }
    }

    public KakaoLoginEntity getKakaoInfo(String kakaoAccessToken) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        // POST 방식으로 API 서버에 요청 후 response 받아옴
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me", // "https://kapi.kakao.com/v2/user/me"
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
                    .id(kakaoAccountDto.getId())
                    .email(kakaoAccountDto.getKakao_account().getEmail())
                    .kakaoName(kakaoAccountDto.getKakao_account().getProfile().getNickname())
                    .build();
        }
        // 처음 로그인 하는 경우
        else {
            return KakaoLoginEntity.builder()
                    .id(kakaoAccountDto.getId())
                    .email(kakaoAccountDto.getKakao_account().getEmail())
                    .kakaoName(kakaoAccountDto.getKakao_account().getProfile().getNickname())
                    .build();
        }
    }
}

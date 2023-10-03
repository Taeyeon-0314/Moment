package com.mo.moment.jwt;

import com.mo.moment.dto.kakaoDto.KakaoTokenSaveDto;
import com.mo.moment.jwt.dto.Token;
import com.mo.moment.service.kakaoService.CustomUserDetailsService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenProvider {
    private final CustomUserDetailsService customUserDetailsService;

    // 액세스토큰 만료 시간
    @Value("${app.auth.tokenExpiry}")
    private Long accessTokenExpiry;
    // 리프레시 토큰 만료 시간
    @Value("${app.auth.refreshTokenExpiry}")
    private Long refreshTokenExpiry;
    //비밀키
    @Value("${app.auth.tokenSecret}")
    private String secretKey;
    // 역할 정보 저장 필드
    private String ROLES = "roles";

//    클래스 생성 후 비밀키 base64 인코딩
    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    //토큰 생성 , id와 역할정보를 이용하여 토큰 생성
    public Token createToken(Long userPk, KakaoTokenSaveDto roles){
        Claims claims = Jwts.claims().setSubject(String.valueOf(userPk));
        claims.put(ROLES,roles);

        Date now = new Date();

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpiry))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpiry))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();

        return Token.builder()
                .grantType("bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .issuedAt(now)
                .accessTokenExpireDate(new Date(now.getTime() + accessTokenExpiry))
                .build();
    }

    // 토큰으로부터 인증 정보 가져오는 메서드
    public Authentication getAuthentication(String token){

        Claims claims = parseClaims(token);
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "",userDetails.getAuthorities());
    }


    //토큰을 파싱하여 토큰에 담긴 데이터을 가져오는 메서드
    public Claims parseClaims(String token){
        try{
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        }catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    //토큰으로부터 사용자 ID를 가져오는 메서드
    public String getUserPk(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public KakaoTokenSaveDto getKakaoToken(String token){
        Object roles = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("roles");
        LinkedHashMap<String, Object> rolesMap = (LinkedHashMap<String, Object>) roles;
        KakaoTokenSaveDto kakaoTokenSaveDto = KakaoTokenSaveDto.builder()
                .access_token(rolesMap.get("access_token").toString())
                .token_type(rolesMap.get("token_type").toString())
                .refresh_token(rolesMap.get("refresh_token").toString())
                .id_token(rolesMap.get("id_token") != null ? rolesMap.get("id_token").toString() : null)
                .expires_in(Integer.parseInt(rolesMap.get("expires_in").toString()))
                .refresh_token_expires_in(Integer.parseInt(rolesMap.get("refresh_token_expires_in").toString()))
                .scope(rolesMap.get("scope").toString())
                .build();
        System.out.println(roles);
        return kakaoTokenSaveDto;
    }

    //HTTP요청으로부터 토큰을 가져오는 메서드
    public String resolveToken(HttpServletRequest request){
        return request.getHeader("X-AUTH-TOKEN");
    }

    //토큰이 유효한지 검사
    public boolean validationToken(String token){
        try{
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        }catch (Exception e){
            return false;
        }
    }

}

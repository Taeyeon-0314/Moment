package com.mo.moment.service.guestService;

import com.mo.moment.dto.guestDto.GuestTokenHostDto;
import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.jwt.dto.GuestToken;
import com.mo.moment.repository.kakaoRepository.KakaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
@Log4j2
@Service
@RequiredArgsConstructor
public class GuestService {
    private final KakaoRepository kakaoRepository;
    private final AuthTokenProvider tokenProvider;
    public ResponseEntity<?> guest(Long host) {
        KakaoLoginEntity kakaoLoginEntity = kakaoRepository.findById(host).orElseThrow();
        GuestToken guestToken = tokenProvider.guestTokenCreate(kakaoLoginEntity.getKakaoId());
        GuestTokenHostDto guestTokenHostDto = GuestTokenHostDto.builder()
                .kakaoName(kakaoLoginEntity.getKakaoName())
                .profile_image(kakaoLoginEntity.getProfile_image())
                .accessToken(guestToken.getAccessToken())
                .issuedAt(guestToken.getIssuedAt())
                .accessTokenExpireDate(guestToken.getAccessTokenExpireDate())
                .build();
        return ResponseEntity.status(200).body(guestTokenHostDto);
    }
}

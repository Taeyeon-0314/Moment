package com.mo.moment.service.kakaoService;

import com.mo.moment.repository.kakaoRepository.KakaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final KakaoRepository kakaoRepository;

    // 사용자의 이름으로 사용자 정보 조회
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {

        // userPk로 카카오 사용자 정보를 데이터베이스에서 검색
        // 찾을수 없으면 예외를 발생
        return (UserDetails) kakaoRepository.findById(Long.parseLong(userPk))
                .orElseThrow(UserNotFoundCException::new);
    }
}

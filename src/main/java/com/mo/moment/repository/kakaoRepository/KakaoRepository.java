package com.mo.moment.repository.kakaoRepository;

import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoRepository extends JpaRepository<KakaoLoginEntity, Long> {

    Optional<KakaoLoginEntity> findByKakaoId(Long id);
}

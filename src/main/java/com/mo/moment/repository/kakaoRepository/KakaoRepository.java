package com.mo.moment.repository.kakaoRepository;

import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoRepository extends JpaRepository<KakaoLoginEntity, Long> {

}

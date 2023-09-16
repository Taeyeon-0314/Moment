package com.mo.moment.repository.jwtRepository;

import com.mo.moment.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JwtRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenKey(Long key);
}

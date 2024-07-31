package com.mo.moment.repository.boardRepository;

import com.mo.moment.entity.boardEntity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<BoardEntity , Long> {
    @Query("SELECT b FROM BoardEntity b WHERE b.kakaoLoginEntity.kakaoId = :kakaoId AND b.content <> ''")
    Page<BoardEntity> findByKakaoLoginEntity_KakaoId(Long kakaoId , Pageable pageable);
    @Query("SELECT COUNT(b) FROM BoardEntity b WHERE b.kakaoLoginEntity.kakaoId = :kakaoId AND b.content <> ''")
    Long countBoardEntityByKakaoLoginEntityKakaoId(Long kakaoId);
}

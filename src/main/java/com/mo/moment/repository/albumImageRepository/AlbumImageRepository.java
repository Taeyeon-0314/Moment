package com.mo.moment.repository.albumImageRepository;

import com.mo.moment.entity.albumEntity.AlbumImageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumImageRepository extends JpaRepository<AlbumImageEntity,Long> {
    Page<AlbumImageEntity> findByKakaoId(Long kakaoId , Pageable pageable);

    List<AlbumImageEntity> findByBoardEntity_BoardId(Long boardId);
}

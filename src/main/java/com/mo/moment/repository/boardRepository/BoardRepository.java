package com.mo.moment.repository.boardRepository;

import com.mo.moment.entity.boardEntity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity , Long> {
}

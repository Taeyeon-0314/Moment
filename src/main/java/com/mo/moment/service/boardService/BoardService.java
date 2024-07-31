package com.mo.moment.service.boardService;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.mo.moment.dto.boardDto.*;
import com.mo.moment.entity.albumEntity.AlbumImageEntity;
import com.mo.moment.entity.boardEntity.BoardEntity;
import com.mo.moment.repository.albumImageRepository.AlbumImageRepository;
import com.mo.moment.repository.boardRepository.BoardRepository;
import com.mo.moment.repository.kakaoRepository.KakaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    // Amazon S3 버킷 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // Amazon S3 클라이언트와 앨범 이미지 레포지토리를 주입받는 생성자
    private final AmazonS3Client amazonS3Client;

    private final AlbumImageRepository imageRepository;
    private final KakaoRepository kakaoRepository;

    public Long saveBoard(String content, String hostId) {
        BoardEntity boardEntity = BoardEntity.builder()
                .content(content)
                .kakaoLoginEntity(kakaoRepository.findById(Long.valueOf(hostId)).get())
                .build();
        return boardRepository.save(boardEntity).getBoardId();
    }

    public ResponseEntity<BoardResponseDto> findByBoardIdAndContent(Long boardId) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(boardId);
        if (optionalBoardEntity.isPresent()) {
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardResponseDto boardDto = BoardResponseDto.builder()
                    .content(boardEntity.getContent())
                    .build();
            return ResponseEntity.status(200).body(boardDto);
        } else {
            return null;
        }
    }

    public ResponseEntity<Boolean> findByBoardIdAndImageDelete(Long boardId, String kakaoId) {
        List<AlbumImageEntity> imageEntityList = imageRepository.findByBoardEntity_BoardId(boardId);

        for (AlbumImageEntity imageEntity : imageEntityList) {
            if (imageEntity.getKakaoId().equals(Long.valueOf(kakaoId))) {
                deleteImageFromS3(imageEntity.getAccessUrl());
                deleteImageFromS3(imageEntity.getResizeUrl());
            } else {
                return ResponseEntity.status(401).body(false);
            }
        }
        boardRepository.deleteById(boardId);
        return ResponseEntity.status(200).body(true);
    }

    private void deleteImageFromS3(String imageUrl) {
        String splitStr = ".com/";
        String fileName = imageUrl.substring(imageUrl.lastIndexOf(splitStr) + splitStr.length());
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    public ResponseEntity<?> allview(String kakaoId, Pageable pageable) {
        Page<BoardEntity> boardEntityPage = boardRepository.findByKakaoLoginEntity_KakaoId(Long.valueOf(kakaoId), pageable);
        Page<BoardPageRequestDto> boardPageView = boardEntityPage.map(boardEntity -> {
            List<BoardImageViewDto> boardImageViewDtoList = new ArrayList<>();
            for (AlbumImageEntity albumImageEntity : boardEntity.getImageEntities()) {
                BoardImageViewDto boardImageViewDto = BoardImageViewDto.builder()
                        .imageId(albumImageEntity.getId())
                        .originName(albumImageEntity.getOriginName())
                        .metaDateTime(albumImageEntity.getMetaDateTime())
                        .accessUrl(albumImageEntity.getAccessUrl())
                        .build();
                boardImageViewDtoList.add(boardImageViewDto);
            }
            return BoardPageRequestDto.builder()
                    .board_id(boardEntity.getBoardId())
                    .content(boardEntity.getContent())
                    .dateTime(boardEntity.getMetaDateTime())
                    .boardImageViewDtoList(boardImageViewDtoList)
                    .build();
        });
        BoardViewPageDto pageResponse = new BoardViewPageDto();
        pageResponse.setPageNumber(boardPageView.getNumber());
        pageResponse.setPageSize(boardPageView.getSize());
        pageResponse.setTotalPages(boardPageView.getTotalPages());
        pageResponse.setLast(boardPageView.isLast());
        pageResponse.setBoard(boardPageView.getContent());
        return ResponseEntity.status(200).body(pageResponse);
    }

    public ResponseEntity<?> boardImageView(String kakaoId, Long boardId) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(boardId);
        if (boardEntityOptional.isPresent()) {
            BoardEntity boardEntity = boardEntityOptional.get();
            if (boardEntity.getKakaoLoginEntity().getKakaoId().equals(Long.valueOf(kakaoId))) {
                List<BoardImageViewDto> boardImageViewDtoList = new ArrayList<>();
                for (AlbumImageEntity albumImageEntity : boardEntity.getImageEntities()) {
                    BoardImageViewDto boardImageViewDto = BoardImageViewDto.builder()
                            .imageId(albumImageEntity.getId())
                            .originName(albumImageEntity.getOriginName())
                            .metaDateTime(albumImageEntity.getMetaDateTime())
                            .accessUrl(albumImageEntity.getAccessUrl())
                            .build();
                    boardImageViewDtoList.add(boardImageViewDto);
                }
                BoardDetailViewDto boardDetailViewDto = BoardDetailViewDto.builder()
                        .boardId(boardEntity.getBoardId())
                        .content(boardEntity.getContent())
                        .accessUrl(boardImageViewDtoList)
                        .build();
                return ResponseEntity.status(200).body(boardDetailViewDto);
            } else {
                return ResponseEntity.status(403).body("권한없음");
            }
        } else {
            return ResponseEntity.status(401).body("게시판없음");
        }
    }

    public void boardMetadateSave(Long boardId) {
        List<AlbumImageEntity> albumImageEntity = imageRepository.findByBoardEntity_BoardIdOrderByMetaDateTime(boardId);
        Optional<BoardEntity> boardEntity = boardRepository.findById(boardId);
        BoardEntity boardEntity1 = boardEntity.get();
        boardEntity1.setMetaDateTime(albumImageEntity.get(0).getMetaDateTime());
        boardRepository.save(boardEntity1);
    }
}

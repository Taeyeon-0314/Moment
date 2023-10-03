package com.mo.moment.service.boardService;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.mo.moment.dto.boardDto.BoardResponseDto;
import com.mo.moment.entity.albumEntity.AlbumImageEntity;
import com.mo.moment.entity.boardEntity.BoardEntity;
import com.mo.moment.repository.albumImageRepository.AlbumImageRepository;
import com.mo.moment.repository.boardRepository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public Long saveBoard(String content) {
        BoardEntity boardEntity = BoardEntity.builder()
                .content(content)
                .build();

        return boardRepository.save(boardEntity).getBoardId();
    }

    public ResponseEntity<BoardResponseDto> findByBoardIdAndContent(Long boardId) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(boardId);
        if(optionalBoardEntity.isPresent()){
            BoardEntity boardEntity = optionalBoardEntity.get();
            BoardResponseDto boardDto = BoardResponseDto.builder()
                    .content(boardEntity.getContent())
                    .build();
            return ResponseEntity.status(200).body(boardDto);
        }else{
            return null;
        }
    }

    public ResponseEntity<Boolean> findByBoardIdAndImageDelete(Long boardId) {
        List<AlbumImageEntity> imageEntityList = imageRepository.findByBoardEntity_BoardId(boardId);
        for (AlbumImageEntity imageEntity : imageEntityList){
            deleteImageFromS3(imageEntity.getAccessUrl());
            deleteImageFromS3(imageEntity.getResizeUrl());
        }
        boardRepository.deleteById(boardId);
        return ResponseEntity.status(200).body(true);
    }

    private void deleteImageFromS3(String imageUrl){
        String splitStr = ".com/";
        String fileName = imageUrl.substring(imageUrl.lastIndexOf(splitStr) + splitStr.length());
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName , fileName));
    }
}

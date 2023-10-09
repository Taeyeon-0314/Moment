package com.mo.moment.service.albumImageService;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.mo.moment.dto.albumImageDto.AlbumImageDto;
import com.mo.moment.dto.albumImageDto.AlbumImageViewPageDto;
import com.mo.moment.dto.albumImageDto.AlbumImageViewRequestDto;
import com.mo.moment.entity.albumEntity.AlbumImageEntity;
import com.mo.moment.entity.boardEntity.BoardEntity;
import com.mo.moment.repository.albumImageRepository.AlbumImageRepository;
import com.mo.moment.repository.boardRepository.BoardRepository;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumImageService {

    // Amazon S3 버킷 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // Amazon S3 클라이언트와 앨범 이미지 레포지토리를 주입받는 생성자
    private final AmazonS3Client amazonS3Client;

    private final AlbumImageRepository imageRepository;

    private final BoardRepository boardRepository;

    // 여러 이미지를 저장하고 그 결과 URL을 반환하는 메서드
    @Transactional
    public ResponseEntity<?> saveImages(AlbumImageDto albumImageDto, Long boardId) {
        List<String> resultList = new ArrayList<>();

        // 전달된 MultipartFile 리스트에서 각 이미지를 저장하고 URL을 리스트에 추가
        for (MultipartFile multipartFile : albumImageDto.getImages()) {
            String value = saveImage(multipartFile , boardId);
            resultList.add(value);
        }

        return ResponseEntity.status(200).body(resultList);
    }

    // 이미지를 저장하고 저장된 이미지의 URL을 반환하는 메서드
    @Transactional
    public String saveImage(MultipartFile multipartFile,Long boardId) {

        // 업로드된 파일의 원래 이름을 가져옴
        String originalName = multipartFile.getOriginalFilename();
        // AlbumImage 엔티티를 생성하고 저장된 이미지의 파일 이름을 생성
        AlbumImageEntity image = new AlbumImageEntity(originalName);
        String filename = image.getStoredName();

        try {
            // 이미지의 메타데이터를 설정 (컨텐츠 타입 및 크기)
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getInputStream().available());

            // Amazon S3에 이미지를 업로드
            amazonS3Client.putObject(bucketName, filename, multipartFile.getInputStream(), objectMetadata);

            // 업로드된 이미지의 접근 URL을 가져와 AlbumImage 엔티티에 저장
            String accessUrl = amazonS3Client.getUrl(bucketName, filename).toString();
            image.setAccessUrl(accessUrl);


            try (InputStream inputStream = multipartFile.getInputStream()) {
                // 이미지 파일에서 메타데이터를 읽습니다.
                Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

                // EXIF 서브 디렉토리에서 날짜와 시간 정보를 가져옵니다.
                ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
                if (directory != null) {
                    Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                    if (date == null) {
                        date = new Date();
                    }
                    image.setMetaDateTime(date);
                    // AlbumImage 엔티티를 저장
                    // 리사이즈된 이미지를 생성하여 S3에 업로드
                    String resizedFilename = filename + "_resized"; // 리사이즈된 이미지 파일 이름
                    File resizedImageFile = resizeImage(multipartFile);
                    amazonS3Client.putObject(bucketName, resizedFilename, resizedImageFile);
                    // 리사이즈된 이미지의 접근 URL을 가져와 AlbumImage 엔티티에 저장
                    String resizeUrl = amazonS3Client.getUrl(bucketName, resizedFilename).toString();
                    image.setResizeUrl(resizeUrl);
                    BoardEntity boardEntity = boardRepository.findById(boardId).get();
                    image.setBoardEntity(boardEntity);
                    imageRepository.save(image);

                    //로컬 리사이징 이미지 파일 삭제

                    if(resizedImageFile != null && resizedImageFile.exists()){
                        if(resizedImageFile.delete()){
                            System.out.println("이미지 삭제됨");
                        }else {
                            System.out.println("이미지 삭제 실패");
                        }
                    }else {
                        System.out.println("이미지 파일이 없음");
                    }
                    //저장된 이미지의 접근 URL 반환
                    return image.getAccessUrl();

                } else {
                    Date nowDate = new Date();
                    image.setMetaDateTime(nowDate);
                    // 리사이즈된 이미지를 생성하여 S3에 업로드
                    String resizedFilename = filename + "_resized"; // 리사이즈된 이미지 파일 이름
                    File resizedImageFile = resizeImage(multipartFile);
                    amazonS3Client.putObject(bucketName, resizedFilename, resizedImageFile);
                    // 리사이즈된 이미지의 접근 URL을 가져와 AlbumImage 엔티티에 저장
                    String resizeUrl = amazonS3Client.getUrl(bucketName, resizedFilename).toString();
                    image.setResizeUrl(resizeUrl);

                    BoardEntity boardEntity = boardRepository.findById(boardId).get();
                    image.setBoardEntity(boardEntity);
                    // AlbumImage 엔티티를 저장
                    imageRepository.save(image);

                    //로컬 리사이징 이미지 파일 삭제
                    if(resizedImageFile != null && resizedImageFile.exists()){
                        if(resizedImageFile.delete()){
                            System.out.println("이미지 삭제됨");
                        }else {
                            System.out.println("이미지 삭제 실패");
                        }
                    }else {
                        System.out.println("이미지 파일이 없음");
                    }

                    //저장된 이미지의 접근 URL 반환
                    return image.getAccessUrl();

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ImageProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            // 예외 처리가 필요한 경우 (IOException 발생 시) 아무것도 수행하지 않음
        }
//         저장된 이미지의 접근 URL 반환
        return image.getAccessUrl();

    }

    // 이미지 리사이징 메서드
    private File resizeImage(MultipartFile originalImage) throws IOException {
        // 이미지 리사이징 (예: 가로 300px로 리사이즈)
        File resizedFile = new File("resized_" + originalImage.getOriginalFilename());
        Thumbnails.of(originalImage.getInputStream())
                .size(300, 300)
                .toFile(resizedFile);
        return resizedFile;
    }

    public ResponseEntity<AlbumImageViewPageDto> allView(String kakaoId, Pageable pageable) {
        Page<AlbumImageEntity> imageEntityPage = imageRepository.findByKakaoId(Long.valueOf(kakaoId), pageable);

        Page<AlbumImageViewRequestDto> albumImageViewDto = imageEntityPage.map(albumImageEntity -> {
            BoardEntity boardEntity = boardRepository.findById(albumImageEntity.getBoardEntity().getBoardId()).get();
            if(boardEntity.getContent().length() > 0){
                return AlbumImageViewRequestDto.builder()
                        .id(albumImageEntity.getId())
                        .originName(albumImageEntity.getOriginName())
                        .boardId(albumImageEntity.getBoardEntity().getBoardId())
                        .metaDateTime(albumImageEntity.getMetaDateTime())
                        .accessUrl(albumImageEntity.getAccessUrl())
                        .resizeUrl(albumImageEntity.getResizeUrl())
                        .contentCheck(true)
                        .build();
            }else{
               return AlbumImageViewRequestDto.builder()
                        .id(albumImageEntity.getId())
                       .originName(albumImageEntity.getOriginName())
                        .boardId(albumImageEntity.getBoardEntity().getBoardId())
                        .metaDateTime(albumImageEntity.getMetaDateTime())
                        .accessUrl(albumImageEntity.getAccessUrl())
                        .resizeUrl(albumImageEntity.getResizeUrl())
                        .contentCheck(false)
                        .build();
            }

        });

        AlbumImageViewPageDto pageResponse = new AlbumImageViewPageDto();
        pageResponse.setContent(albumImageViewDto.getContent());
        pageResponse.setPageSize(albumImageViewDto.getSize());
        pageResponse.setPageNumber(albumImageViewDto.getNumber());
        pageResponse.setTotalPages(albumImageViewDto.getTotalPages());
        pageResponse.setLast(albumImageViewDto.isLast());
        return ResponseEntity.status(200).body(pageResponse);
    }

    public ResponseEntity<?> deleteImage(Long imageId, String kakaoId) {
        AlbumImageEntity albumImageEntity = imageRepository.findById(imageId).get();
        if (albumImageEntity.getKakaoId().equals(Long.valueOf(kakaoId))){
            deleteImageFromS3(albumImageEntity.getAccessUrl());
            deleteImageFromS3(albumImageEntity.getResizeUrl());
            imageRepository.deleteById(imageId);
            return ResponseEntity.status(200).body("이미지가 삭제되었습니다.");
        }else {
            return ResponseEntity.status(401).body("이미지 삭제 실패");
        }
    }

    private void deleteImageFromS3(String imageUrl){
        String splitStr = ".com/";
        String fileName = imageUrl.substring(imageUrl.lastIndexOf(splitStr) + splitStr.length());
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName , fileName));
    }
}

package com.mo.moment.service.mypageService;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mo.moment.dto.myPageDto.MyPageMainResponseDto;
import com.mo.moment.entity.kakaoEntity.KakaoLoginEntity;
import com.mo.moment.jwt.entity.RefreshToken;
import com.mo.moment.repository.albumImageRepository.AlbumImageRepository;
import com.mo.moment.repository.boardRepository.BoardRepository;
import com.mo.moment.repository.jwtRepository.JwtRepository;
import com.mo.moment.repository.kakaoRepository.KakaoRepository;
import com.mo.moment.service.kakaoService.KakaoLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final BoardRepository boardRepository;
    private final AlbumImageRepository imageRepository;
    private final KakaoRepository kakaoRepository;
    private final KakaoLoginService kakaoLoginService;
    private final AmazonS3Client amazonS3Client;
    private final JwtRepository jwtRepository;

    @Value("${cloud.aws.s3.user-image-bucket}")
    private String bucketName;

    public MyPageMainResponseDto mypage(String kakaoId) {
        KakaoLoginEntity kakaoLoginEntity = kakaoRepository.findById(Long.valueOf(kakaoId)).get();
        Long boardCount = boardRepository.countBoardEntityByKakaoLoginEntityKakaoId(Long.valueOf(kakaoId));
        Long imageCount = imageRepository.countAlbumImageEntityByKakaoId(Long.valueOf(kakaoId));
        System.out.println("나는게시판수"+boardCount+"나는 이미지수"+imageCount);
        MyPageMainResponseDto myPageMainResponseDto = MyPageMainResponseDto.builder()
                .kakaoId(kakaoLoginEntity.getKakaoId())
                .kakaoName(kakaoLoginEntity.getKakaoName())
                .profileImage(kakaoLoginEntity.getProfile_image())
                .messageCount(boardCount)
                .imageCount(imageCount)
                .build();
        return myPageMainResponseDto;
    }

    public ResponseEntity<?> imageNickNameSave(String kakaoId, String nickName, MultipartFile imageFile, HttpServletRequest request) {
        KakaoLoginEntity kakaoLoginEntity = kakaoRepository.findById(Long.valueOf(kakaoId)).get();
        kakaoLoginEntity.setKakaoName(nickName);
        String originalName = imageFile.getOriginalFilename();
        String filename = getFileName(originalName);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(imageFile.getContentType());
            objectMetadata.setContentLength(imageFile.getInputStream().available());
            amazonS3Client.putObject(new PutObjectRequest(bucketName,filename,imageFile.getInputStream(),objectMetadata));
            String accessUrl = amazonS3Client.getUrl(bucketName, filename).toString();
            kakaoLoginEntity.setProfile_image(accessUrl);
        }catch (IOException e){
            throw new RuntimeException("이미지 업로드에 실패했습니다.");
        }
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return kakaoLoginService.reissue(refreshToken);
    }
    public String extractExtension(String originName) {
        int index = originName.lastIndexOf('.');
        return originName.substring(index, originName.length());
    }
    public String getFileName(String originName) {
        return UUID.randomUUID() + "." + extractExtension(originName);
    }
    public ResponseEntity<?> NuckNameSave(String kakaoId, String nickName, HttpServletRequest request) {
        KakaoLoginEntity kakaoLoginEntity = kakaoRepository.findById(Long.valueOf(kakaoId)).get();
        kakaoLoginEntity.setKakaoName(nickName);
        kakaoRepository.save(kakaoLoginEntity);

        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        return kakaoLoginService.reissue(refreshToken);
    }

    public ResponseEntity<?> logout(String kakaoId) {
        Optional<RefreshToken> byKakaoLoginEntityKakaoId = jwtRepository.findByKakaoLoginEntity_KakaoId(Long.valueOf(kakaoId));
        if (byKakaoLoginEntityKakaoId.isPresent()) {
            jwtRepository.deleteById(byKakaoLoginEntityKakaoId.get().getId());
        }
        return ResponseEntity.status(200).build();
    }
}

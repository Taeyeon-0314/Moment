package com.mo.moment.controller;

import com.mo.moment.config.KoreaTime;
import com.mo.moment.dto.albumImageDto.AlbumImageDto;
import com.mo.moment.dto.albumImageDto.AlbumImageViewPageDto;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.service.albumImageService.AlbumImageService;
import com.mo.moment.service.boardService.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {

    private final AlbumImageService albumImageService;
    private final AuthTokenProvider authTokenProvider;
    private final BoardService boardService;

    @PutMapping("/image")
    public ResponseEntity<?> saveImage(@ModelAttribute AlbumImageDto albumImageDto){
        if(albumImageDto.getImages().size()>5){
            return ResponseEntity.status(400).body("파일은 5개까지만 가능해요!");
        }else {
            String content = albumImageDto.getContent();
            Long boardId = boardService.saveBoard(content);
            return albumImageService.saveImages(albumImageDto,boardId);
        }
    }

    @DeleteMapping("/{image_id}")
    public ResponseEntity<?> imageDelete(@PathVariable Long image_id, HttpServletRequest request) {
        String header = request.getHeader("X-AUTH-TOKEN");
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("[imageDelete]");
        log.info("KST Date: {}, image_id: {}", zonedDateTime, image_id);
        return albumImageService.deleteImage(image_id , kakaoId);
    }

    @GetMapping("/imageview")
    public ResponseEntity<AlbumImageViewPageDto> imageView(HttpServletRequest request ,
                                                                 @PageableDefault(page = 0, size = 15 , sort = "metaDateTime" ,
                                                                     direction = Sort.Direction.DESC) Pageable pageable){

        String header = request.getHeader("X-AUTH-TOKEN");
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("[imageView]");
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);
        return albumImageService.allView(kakaoId, pageable);
    }
}

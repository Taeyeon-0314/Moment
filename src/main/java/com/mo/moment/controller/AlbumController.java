package com.mo.moment.controller;

import com.mo.moment.config.KoreaTime;
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

    @DeleteMapping("/{image_id}")
    public ResponseEntity<?> imageDelete(@PathVariable Long image_id, HttpServletRequest request) {
        log.info("[imageDelete]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);
        log.info("KST Date: {}, image_id: {}", zonedDateTime, image_id);
        return albumImageService.deleteImage(image_id , kakaoId);
    }

    @GetMapping("/imageview")
    public ResponseEntity<AlbumImageViewPageDto> imageView(HttpServletRequest request ,
                                                                 @PageableDefault(page = 0, size = 15 , sort = "metaDateTime" ,
                                                                     direction = Sort.Direction.DESC) Pageable pageable){
        log.info("[imageView]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);
        return albumImageService.allView(kakaoId, pageable);
    }
}

package com.mo.moment.controller;

import com.mo.moment.dto.albumImageDto.AlbumImageDto;
import com.mo.moment.dto.albumImageDto.AlbumImageViewPageDto;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.service.albumImageService.AlbumImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/album")
public class AlbumController {

    private final AlbumImageService albumImageService;
    private final AuthTokenProvider authTokenProvider;

    @PutMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public List<String> saveImage(@ModelAttribute AlbumImageDto albumImageDto){

        return albumImageService.saveImages(albumImageDto);
    }

    @GetMapping("/imageview")
    public ResponseEntity<AlbumImageViewPageDto> imageView(HttpServletRequest request ,
                                                                 @PageableDefault(page = 0, size = 15 , sort = "metaDateTime" ,
                                                                     direction = Sort.Direction.DESC) Pageable pageable){
        String header = request.getHeader("X-AUTH-TOKEN");
        String kakaoId = authTokenProvider.getUserPk(header);
        return albumImageService.allView(kakaoId, pageable);
    }
}

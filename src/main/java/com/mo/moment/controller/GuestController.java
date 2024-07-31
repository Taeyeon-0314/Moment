package com.mo.moment.controller;

import com.mo.moment.dto.albumImageDto.AlbumImageDto;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.service.albumImageService.AlbumImageService;
import com.mo.moment.service.boardService.BoardService;
import com.mo.moment.service.guestService.GuestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/guest")
public class GuestController {
    private final GuestService guestService;
    private final BoardService boardService;
    private final AlbumImageService albumImageService;
    private final AuthTokenProvider authTokenProvider;

    @GetMapping("/host")
    private ResponseEntity<?> quest(@RequestParam("host") Long host) {
        log.info("[GuestLogin]");
    return guestService.guest(host);
    }

    @PutMapping("/image")
    public ResponseEntity<?> saveImage(@ModelAttribute AlbumImageDto albumImageDto, HttpServletRequest request){
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String hostId = authTokenProvider.getUserPk(header);
        log.info("[GuestImageSave]");
        List<MultipartFile> imagesFile = new ArrayList<>();
        if(albumImageDto.getImage1() != null &&  !albumImageDto.getImage1().isEmpty()){
            log.info(albumImageDto.getImage1().getOriginalFilename());
            imagesFile.add(albumImageDto.getImage1());
        }
        if(albumImageDto.getImage2() != null && !albumImageDto.getImage2() .isEmpty()){
            log.info(albumImageDto.getImage2().getOriginalFilename());

            imagesFile.add(albumImageDto.getImage2());
        }
        if(albumImageDto.getImage3() != null && !albumImageDto.getImage3().isEmpty()){
            log.info(albumImageDto.getImage3().getOriginalFilename());

            imagesFile.add(albumImageDto.getImage3());
        }
        if(albumImageDto.getImage4() != null && !albumImageDto.getImage4() .isEmpty()){
            log.info(albumImageDto.getImage4().getOriginalFilename());

            imagesFile.add(albumImageDto.getImage4());
        }
        if(albumImageDto.getImage5() != null && !albumImageDto.getImage5() .isEmpty()){
            log.info(albumImageDto.getImage5().getOriginalFilename());

            imagesFile.add(albumImageDto.getImage5());
        }
//        if(albumImageDto.get.size()>5){
//            return ResponseEntity.status(400).body("파일은 5개까지만 가능해요!");
//        }else {
            String content = albumImageDto.getContent();
            Long boardId = boardService.saveBoard(content,hostId);
            ResponseEntity<?> responseEntity = albumImageService.saveImages(imagesFile, boardId, hostId);
            boardService.boardMetadateSave(boardId);
            return responseEntity;
//        }
    }
}

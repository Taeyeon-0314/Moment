package com.mo.moment.controller;

import com.mo.moment.config.KoreaTime;
import com.mo.moment.dto.boardDto.BoardResponseDto;
import com.mo.moment.jwt.AuthTokenProvider;
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
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final AuthTokenProvider authTokenProvider;

    @GetMapping("/{board_id}")
    public ResponseEntity<BoardResponseDto> boardView(@PathVariable Long board_id , HttpServletRequest request){
        log.info("[boardView]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);
        return boardService.findByBoardIdAndContent(board_id);
    }

    @DeleteMapping("/{board_id}")
    public ResponseEntity<Boolean> boardDelete(@PathVariable Long board_id, HttpServletRequest request){
        log.info("[boardDelete]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);
        return boardService.findByBoardIdAndImageDelete(board_id,kakaoId);
    }

    @GetMapping(value = {"","/"})
    public ResponseEntity<?> boardList(HttpServletRequest request , @PageableDefault(page=0, size=15 , sort = "metaDateTime" ,
            direction = Sort.Direction.DESC) Pageable pageable){
        log.info("[boardList]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);
        return boardService.allview(kakaoId,pageable);
    }

    @GetMapping("/image/{board_id}")
    public ResponseEntity<?> boardImageList(HttpServletRequest request, @PathVariable Long board_id){
        log.info("[boardImageList]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);
        return boardService.boardImageView(kakaoId,board_id);
    }
}

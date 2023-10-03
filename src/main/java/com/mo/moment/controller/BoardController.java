package com.mo.moment.controller;

import com.mo.moment.config.KoreaTime;
import com.mo.moment.dto.boardDto.BoardResponseDto;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.service.boardService.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> boardView(@PathVariable Long boardId , HttpServletRequest request){

        String header = request.getHeader("X-AUTH-TOKEN");
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("[boardView]");
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);

        return boardService.findByBoardIdAndContent(boardId);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Boolean> boardDelete(@PathVariable Long boardId, HttpServletRequest request){

        String header = request.getHeader("X-AUTH-TOKEN");
        String kakaoId = authTokenProvider.getUserPk(header);
        KoreaTime koreaTime = new KoreaTime();
        ZonedDateTime zonedDateTime = koreaTime.koreaDateTime();
        log.info("[boardDelete]");
        log.info("KST Date: {}, kakaoId: {}", zonedDateTime, kakaoId);

        return boardService.findByBoardIdAndImageDelete(boardId);
    }
}

package com.mo.moment.controller;

import com.mo.moment.dto.myPageDto.MyPageMainResponseDto;
import com.mo.moment.jwt.AuthTokenProvider;
import com.mo.moment.service.mypageService.MyPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {
    private final AuthTokenProvider authTokenProvider;
    private final MyPageService myPageService;

    @GetMapping(value = {"","/"})
    private ResponseEntity<MyPageMainResponseDto> myPage(HttpServletRequest request){
        log.info("[MyPage]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        log.info("kakaoId: {}", kakaoId);

        MyPageMainResponseDto myPageMainResponseDto = myPageService.mypage(kakaoId);
        return ResponseEntity.status(200).body(myPageMainResponseDto);
    }

    @PutMapping(value = {"","/"})
    private ResponseEntity<?> userEdit(HttpServletRequest request, @RequestPart(value = "nickName") String nickName,
                                                                  @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {
        log.info("[MyPageEdit]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        log.info("kakaoId: {}", kakaoId);

        if(imageFile != null && !imageFile.getOriginalFilename().equals("")){
            return myPageService.imageNickNameSave(kakaoId,nickName,imageFile,request);
        }else{
            return myPageService.NuckNameSave(kakaoId,nickName,request);
        }
    }

    @PostMapping("/logout")
    private ResponseEntity<?> logOut(HttpServletRequest request, HttpServletResponse response) {
        log.info("[MyPageLogOut]");
        String header = request.getHeader(authTokenProvider.loginAccessToken);
        String kakaoId = authTokenProvider.getUserPk(header);
        log.info("kakaoId: {}", kakaoId);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return myPageService.logout(kakaoId);
    }

}

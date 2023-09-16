package com.mo.moment.jwt.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class MyController {
    @ExceptionHandler(NullPointerException.class)
    public Object nullEx(Exception e) {
        System.out.println(e.getClass());
        return "Exception";
    }

}

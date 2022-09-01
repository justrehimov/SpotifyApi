package com.spotify.controller;

import com.spotify.dto.response.ResponseModel;
import com.spotify.exception.SpotifyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({SpotifyException.class, RuntimeException.class, Exception.class})
    public ResponseModel<Object> handle(Exception ex){
        return ResponseModel.builder()
                .error(true)
                .message(ex.getMessage())
                .build();
    }


}

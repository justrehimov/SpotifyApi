package com.spotify.controller;

import com.spotify.dto.response.ResponseModel;
import com.spotify.exception.SpotifyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.Binding;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({SpotifyException.class, RuntimeException.class, Exception.class})
    public ResponseModel<Object> handle(Exception ex){
        return ResponseModel.builder()
                .error(true)
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseModel<Object> handle(BindingResult bindingResult){
        return ResponseModel.builder()
                .result(null)
                .error(true)
                .message(bindingResult.getFieldError().getDefaultMessage())
                .build();
    }


}

package com.kincoom.pay.springboot.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/*
* 비즈니스 로직에서 RestException으로 예외를 발생시켰을 때
* ExceptionHandler가 붙은 매서드에서 캐치하여 이후 로직 공통 처리
* */

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RestException.class)
    public ResponseEntity<Map<String, Object>> handler(RestException e) {
        Map<String, Object> resBody = new HashMap<>();
        resBody.put("message", e.getMessage());

        return new ResponseEntity<>(resBody, e.getStatus());
    }

}
package com.example.user_service.exception;

import com.example.common.exception.BaseGlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {
    // În acest moment nu avem nevoie de handlere specifice aici,
    // deoarece toate sunt moștenite din BaseGlobalExceptionHandler.
}

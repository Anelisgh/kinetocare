package com.example.chat_service.exception;

import com.example.common.exception.BaseGlobalExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseGlobalExceptionHandler {
}

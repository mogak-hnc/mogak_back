package com.hnc.mogak.global.exception;

import com.hnc.mogak.global.exception.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ChallengeArticleException.class)
    public ResponseEntity<String> handleChallengeArticleException(ChallengeArticleException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ChallengeException.class)
    public ResponseEntity<String> handleChallengeException(ChallengeException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<String> handleMemberException(MemberException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MogakZoneException.class)
    public ResponseEntity<String> handleMogakZoneException(MogakZoneException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WebSocketException.class)
    public ResponseEntity<String> handleWebSocketException(WebSocketException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WorryException.class)
    public ResponseEntity<String> handleWorryException(WorryException ex) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        log.error("MethodArgumentNotValidException 발생: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<String> buildErrorResponse(Exception ex, HttpStatus status) {
        log.error("{} 발생: {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return new ResponseEntity<>(ex.getMessage(), status);
    }

}

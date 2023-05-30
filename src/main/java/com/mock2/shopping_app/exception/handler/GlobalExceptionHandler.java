package com.mock2.shopping_app.exception.handler;

import com.mock2.shopping_app.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameExistedException.class)
    public ResponseEntity<ExceptionResponse> handleUsernameExistedException(HttpServletRequest request,
                                                                            UsernameExistedException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(HttpServletRequest request,
                                                                           EntityNotFoundException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(HttpServletRequest request,
                                                                        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .message(errors)
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(HttpServletRequest request,
                                                                            IllegalArgumentException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserLoginException.class)
    public ResponseEntity<ExceptionResponse> handleUserLoginException(HttpServletRequest request,
                                                                      UserLoginException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ExceptionResponse> handleTokenRefreshException(HttpServletRequest request,
                                                                         TokenRefreshException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenRequestException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ResponseBody
    public ExceptionResponse handleInvalidTokenRequestException(HttpServletRequest request,
                                                                InvalidTokenRequestException ex) {
        return ExceptionResponse.builder()
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .error(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handleAuthenticationException(HttpServletRequest request,
                                                                           AuthenticationException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidToReviewProductException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidToReviewProductException(HttpServletRequest request,
                                                                                   InvalidToReviewProductException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .error(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                .path(request.getRequestURI())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceededException(HttpServletRequest request,
                                                                                  MaxUploadSizeExceededException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .status(HttpStatus.EXPECTATION_FAILED.value())
                .error(HttpStatus.EXPECTATION_FAILED.getReasonPhrase())
                .path(request.getRequestURI())
                .message("File too large")
                .timestamp(LocalDateTime.now()).build();
        return new ResponseEntity<>(exceptionResponse, HttpStatus.EXPECTATION_FAILED);
    }
}

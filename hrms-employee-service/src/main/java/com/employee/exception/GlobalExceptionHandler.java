package com.employee.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.employee.response.dto.ApiResponseDto;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto> handleResourceNotFound(ResourceNotFoundException ex) {

        ApiResponseDto response = ApiResponseDto.builder()
                .success(false)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponseDto> handleDuplicate(DuplicateResourceException ex) {

        ApiResponseDto response = ApiResponseDto.builder()
                .success(false)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseDto> handleBadRequest(BadRequestException ex) {

        ApiResponseDto response = ApiResponseDto.builder()
                .success(false)
                .message(ex.getMessage())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDto> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ApiResponseDto response = ApiResponseDto.builder()
                .success(false)
                .message("Validation Failed")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponseDto> handleInvalidRequest(
            InvalidRequestException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDto.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .build());
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponseDto> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponseDto.builder()
                        .success(false)
                        .message("Invalid credentials")
                        .build());
    }
    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDto> handleMissingParam(
            MissingServletRequestParameterException ex) {

        return ResponseEntity.badRequest().body(
                ApiResponseDto.builder()
                        .success(false)
                        .message(ex.getParameterName() + " is required")
                        .build());
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseDto> handleAccessDenied(AccessDeniedException ex) {

        ApiResponseDto response = ApiResponseDto.builder()
                .success(false)
                .message("Access Denied")
                .errors("You do not have permission to perform this action.")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleException(Exception ex) {

        ApiResponseDto response = ApiResponseDto.builder()
                .success(false)
                .message("Something went wrong")
                .errors(ex.getMessage()) // Optional: remove in production if you don't want to expose internal messages
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

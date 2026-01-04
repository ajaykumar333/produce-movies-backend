package com.movies.produce_movies.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.movies.produce_movies.constants.ApiError;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400 - Bad Request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 409 - Conflict
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleIllegalState(IllegalStateException ex) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 404 - Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Validation errors
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleValidation(ConstraintViolationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ✅ ONE AND ONLY fallback handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ex.printStackTrace(); // keep this for logs

        return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Unexpected error occurred. Please contact support."
        );
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message) {
        ApiError error = new ApiError(
            status.value(),
            status.getReasonPhrase(),
            message,
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, status);
    }
}

package org.ocean.leaveservice.advices;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.ocean.leaveservice.exceptions.LeaveException;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.responses.ErrorResponse;
import org.ocean.leaveservice.responses.FieldValidationError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LeaveException.class)
    public ResponseEntity<ApiResponse<Void>> handleLeaveException(LeaveException ex, HttpServletRequest request) {

        log.error("Business exception at: {} {}",request.getRequestURI(),ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .error(ex.getError() + " " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.<Void>builder()
                                .success(false)
                                .message(ex.getError() + " " + ex.getMessage())
                                .timestamp(LocalDateTime.now())
                                .error(error)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldValidationError> fieldValidationErrors = ex.getBindingResult().getFieldErrors().stream().map(fieldError ->
                FieldValidationError.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build()
        ).toList();

        log.error("Validation failed at: {} {}",request.getRequestURI(),ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Validation error")
                .path(request.getRequestURI())
                .fieldErrors(fieldValidationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .message("Validation error")
                        .error(errorResponse)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .success(false)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {

        log.error("Constraint violation at: {} {}",request.getRequestURI(),ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .path(request.getRequestURI())
                .error(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .timestamp(LocalDateTime.now())
                        .message(ex.getMessage())
                        .error(errorResponse)
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> genericException(Exception ex, HttpServletRequest request) {

        log.error("Unexcepted error at: {} {}",request.getRequestURI(),ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .path(request.getRequestURI())
                .error("Internal Server Error")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.<Void>builder()
                        .message("Something went wrong. Please contact support.")
                        .success(false)
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .timestamp(LocalDateTime.now())
                        .error(error)
                        .build()
        );
    }
}

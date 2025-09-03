package org.ocean.leaveservice.advices;

import jakarta.servlet.http.HttpServletRequest;
import org.ocean.leaveservice.exceptions.LeaveException;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class LeaveExceptionHandler {

    @ExceptionHandler(LeaveException.class)
    public ResponseEntity<ApiResponse<Void>> handleLeaveException(LeaveException ex, HttpServletRequest req) {
        ErrorResponse error = ErrorResponse.builder()
                .error(ex.getError())
                .path(req.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                  ApiResponse.<Void>builder()
                          .success(false)
                          .message("Error occurred")
                          .timestamp(LocalDateTime.now())
                          .error(error)
                          .statusCode(HttpStatus.BAD_REQUEST.value())
                          .build()
                );
    }
}

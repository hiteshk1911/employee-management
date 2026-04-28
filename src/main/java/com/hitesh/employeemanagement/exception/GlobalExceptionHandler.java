package com.hitesh.employeemanagement.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex) {

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        fieldErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ValidationErrorResponse response =
                ValidationErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("Validation failed")
                        .fieldErrors(fieldErrors)
                        .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex) {

        Map<String, String> fieldErrors = new HashMap<>();

        ex.getConstraintViolations()
                .forEach(violation -> {

                    String field =
                            violation.getPropertyPath()
                                    .toString();

                    fieldErrors.put(
                            field,
                            violation.getMessage()
                    );
                });

        ValidationErrorResponse response =
                ValidationErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("Validation failed")
                        .fieldErrors(fieldErrors)
                        .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {

        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access denied"
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex) {

        String message = ex.getMessage();

        if (message == null) {
            message = "Unexpected error";
        }

        if (message.equalsIgnoreCase("Invalid credentials")) {

            return buildErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    message
            );
        }

        if (message.toLowerCase().contains("refresh token")
                || message.toLowerCase().contains("revoked")
                || message.toLowerCase().contains("expired")) {

            return buildErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    message
            );
        }

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error"
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message) {

        ErrorResponse response =
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
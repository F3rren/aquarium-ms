package it.f3rren.aquarium.inhabitants_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import it.f3rren.aquarium.inhabitants_service.dto.ApiResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, ex.getMessage(), null, null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage()));
        log.warn("Validation failed: {}", fieldErrors);
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, "Validation failed", fieldErrors, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, ex.getMessage(), null, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        log.warn("Type mismatch: {}", message);
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, message, null, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, "An unexpected error occurred. Please try again later.", null, null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

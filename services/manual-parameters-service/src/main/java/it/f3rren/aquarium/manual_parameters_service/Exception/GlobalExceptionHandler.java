package it.f3rren.aquarium.manual_parameters_service.exception;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import it.f3rren.aquarium.manual_parameters_service.dto.ApiResponseDTO;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(false, e.getMessage(), null, null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));
        log.warn("Validation failed: {}", errors);
        return new ResponseEntity<>(new ApiResponseDTO<>(false, "Validation failed", errors, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(false, e.getMessage(), null, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDateTimeParseException(DateTimeParseException e) {
        log.warn("Invalid date format: {}", e.getMessage());
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, "Invalid date format. Expected ISO-8601 (e.g. 2024-01-15T10:30:00)", null, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String msg = "Invalid value '" + e.getValue() + "' for parameter '" + e.getName() + "'";
        log.warn("Type mismatch: {}", msg);
        return new ResponseEntity<>(new ApiResponseDTO<>(false, msg, null, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNoResourceFound(NoResourceFoundException e) {
        log.debug("Static resource not found: {}", e.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(false, "Resource not found", null, null), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed or unreadable request body: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, "Malformed or unreadable request body", null, null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity constraint violated: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ApiResponseDTO<>(false, "Data integrity constraint violated", null, null),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .findFirst()
                .orElse("Constraint violation");
        log.warn("Constraint violation: {}", message);
        return new ResponseEntity<>(new ApiResponseDTO<>(false, message, null, null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleRestClientException(RestClientException ex) {
        log.error("Inter-service communication error: {}", ex.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(false, "External service communication error", null, null), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return new ResponseEntity<>(new ApiResponseDTO<>(false, "An internal error occurred", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

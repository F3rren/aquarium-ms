package it.f3rren.aquarium.aquariums_service.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import it.f3rren.aquarium.aquariums_service.dto.ApiResponseDTO;

/**
 * Global exception handler for the application.
 * It handles different exceptions and provides appropriate responses.
 * It also logs the error details.
 * @author F3rren
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles resource not found exceptions and returns a response with error details.
     * @param ex The resource not found exception that occurred.
     * This is typically thrown when a requested resource is not found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(false, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles validation errors and returns a response with validation details.
     * @param ex The validation exception that occurred during method argument binding.
     * This is typically thrown when @Valid is used on method arguments.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validation failed: {}", fieldErrors);

        ApiResponseDTO<Map<String, String>> response = new ApiResponseDTO<>(
                false, "Validation failed", fieldErrors, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles illegal argument exceptions and returns a response with error details.
     * @param ex The illegal argument exception that occurred.
     * This is typically thrown when a method is called with an invalid argument.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(false, ex.getMessage(), null, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles type mismatch exceptions and returns a response with error details.
     * @param ex The type mismatch exception that occurred.
     * This is typically thrown when a method argument cannot be converted to the required type.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        log.warn("Type mismatch: {}", message);

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(false, message, null, null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles database constraint violations (e.g. unique key violations).
     * Returns 409 Conflict instead of a generic 500.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(false, "Data integrity constraint violated", null, null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handles failures in inter-service HTTP communication.
     * Returns 503 Service Unavailable so callers know the issue is external.
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleRestClientException(RestClientException ex) {
        log.error("Inter-service communication error: {}", ex.getMessage());

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(false, "External service communication error", null, null);
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Handles generic exceptions and returns a response with error details.
     * @param ex The generic exception that occurred.
     * This is typically thrown when an unexpected error occurs during the execution of the application.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        ApiResponseDTO<Void> response = new ApiResponseDTO<>(
                false, "An unexpected error occurred. Please try again later.", null, null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

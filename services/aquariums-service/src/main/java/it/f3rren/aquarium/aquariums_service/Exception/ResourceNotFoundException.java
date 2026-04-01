package it.f3rren.aquarium.aquariums_service.exception;

/**
 * Exception class for resource not found errors.
 * This exception is thrown when a requested resource is not found.
 * It extends the base RuntimeException class.
 * @author F3rren
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a ResourceNotFoundException with the specified error message.
     * @param message The error message describing the resource not found issue.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a ResourceNotFoundException with the specified error message and cause.
     * @param message The error message describing the resource not found issue.
     * @param cause The cause of the resource not found exception.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

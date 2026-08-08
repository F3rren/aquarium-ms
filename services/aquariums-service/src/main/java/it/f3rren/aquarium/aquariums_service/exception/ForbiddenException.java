package it.f3rren.aquarium.aquariums_service.exception;

/**
 * Thrown when the caller is authenticated but not authorized on the specific resource
 * requested - e.g. trying to update or delete an aquarium owned by a different user.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}

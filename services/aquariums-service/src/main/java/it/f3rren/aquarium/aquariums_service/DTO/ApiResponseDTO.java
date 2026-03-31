package it.f3rren.aquarium.aquariums_service.dto;

import lombok.*;

/**
 * DTO for API responses.
 * @param <T> Type of data contained in the response
 * @author F3rren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {

    /**
     * Creates a successful response with data.
     *
     * @param message description of the outcome
     * @param data    response payload
     * @param <T>     payload type
     * @return {@link ApiResponseDTO} with {@code success=true}
     */
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return new ApiResponseDTO<>(true, message, data, null);
    }

    /**
     * Creates a successful response with no data (e.g. delete operations).
     *
     * @param message description of the outcome
     * @return {@link ApiResponseDTO} with {@code success=true} and {@code data=null}
     */
    public static ApiResponseDTO<Void> success(String message) {
        return new ApiResponseDTO<>(true, message, null, null);
    }

    /**
     * Indicates if the operation was successful.
     * true if the operation was successful false otherwise.
     */
    private Boolean success;

    /**
     * A message describing the outcome of the operation. If the operation was not successful,
     * the message should provide details about the error. If successful, the message may indicate
     * the result of the operation.
     */
    private String message;

    /**
     * The data returned by the operation. This field is null if the operation was not successful.
     */
    private T data;

    /**
     * Additional metadata related to the operation. This field is null if not applicable.
     */
    private Object metadata;
}

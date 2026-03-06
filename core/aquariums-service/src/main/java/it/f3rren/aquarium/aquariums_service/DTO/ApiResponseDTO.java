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

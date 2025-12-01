package it.f3rren.aquarium.aquariums_service.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDTO<T> {
    private Boolean success;
    private String message;
    private T data;
    private Object metadata;
}

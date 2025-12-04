package it.f3rren.aquarium.inhabitants_service.dto;

import java.time.LocalDateTime;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InhabitantDetailsDTO {

    private Long id;
    private String type;
    private String commonName;
    private String scientificName;
    private Integer quantity;
    private LocalDateTime addedDate;
    private Object details;
}

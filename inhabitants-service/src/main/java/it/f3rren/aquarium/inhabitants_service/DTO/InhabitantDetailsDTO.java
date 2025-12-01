package it.f3rren.aquarium.inhabitants_service.DTO;

import java.time.LocalDateTime;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InhabitantDetailsDTO {

    private Long id;
    private String type; // "fish" o "coral"
    private String commonName;
    private String scientificName;
    private Integer quantity;
    private LocalDateTime addedDate;
    private Object details; // Fish o Coral completo
}

package it.f3rren.aquarium.aquariums_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ApiResponseDTOAquariumList")
public class ApiResponseDTOAquariumList extends ApiResponseDTO<List<AquariumResponseDTO>> {}

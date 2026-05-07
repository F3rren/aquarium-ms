package it.f3rren.aquarium.species.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ApiResponseDTOCoralList")
public class ApiResponseDTOCoralList extends ApiResponseDTO<List<CoralResponseDTO>> {}

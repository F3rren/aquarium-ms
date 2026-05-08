package it.f3rren.aquarium.species.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ApiResponseDTOFishList")
public class ApiResponseDTOFishList extends ApiResponseDTO<List<FishResponseDTO>> {}

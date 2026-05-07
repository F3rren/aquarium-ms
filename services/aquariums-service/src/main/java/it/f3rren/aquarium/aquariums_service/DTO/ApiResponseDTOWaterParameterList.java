package it.f3rren.aquarium.aquariums_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ApiResponseDTOWaterParameterList")
public class ApiResponseDTOWaterParameterList extends ApiResponseDTO<List<WaterParameterDTO>> {}

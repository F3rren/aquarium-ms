package it.f3rren.aquarium.parameters_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ApiResponseDTOParameterList")
public class ApiResponseDTOParameterList extends ApiResponseDTO<List<ParameterDTO>> {}

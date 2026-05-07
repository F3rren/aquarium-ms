package it.f3rren.aquarium.inhabitants_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "ApiResponseDTOInhabitantDetailsList")
public class ApiResponseDTOInhabitantDetailsList extends ApiResponseDTO<List<InhabitantDetailsDTO>> {}

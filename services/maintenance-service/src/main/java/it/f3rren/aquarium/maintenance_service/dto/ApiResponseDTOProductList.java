package it.f3rren.aquarium.maintenance_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.f3rren.aquarium.maintenance_service.dto.response.ProductDTO;
import java.util.List;

@Schema(name = "ApiResponseDTOProductList")
public class ApiResponseDTOProductList extends ApiResponseDTO<List<ProductDTO>> {}

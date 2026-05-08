package it.f3rren.aquarium.maintenance_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.f3rren.aquarium.maintenance_service.model.ProductCategory;

@Schema(name = "ApiResponseDTOProductCategoryArray")
public class ApiResponseDTOProductCategoryArray extends ApiResponseDTO<ProductCategory[]> {}

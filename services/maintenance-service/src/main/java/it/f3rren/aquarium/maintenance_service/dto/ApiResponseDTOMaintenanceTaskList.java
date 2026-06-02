package it.f3rren.aquarium.maintenance_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.f3rren.aquarium.maintenance_service.dto.response.MaintenanceTaskDTO;
import java.util.List;

@Schema(name = "ApiResponseDTOMaintenanceTaskList")
public class ApiResponseDTOMaintenanceTaskList extends ApiResponseDTO<List<MaintenanceTaskDTO>> {}

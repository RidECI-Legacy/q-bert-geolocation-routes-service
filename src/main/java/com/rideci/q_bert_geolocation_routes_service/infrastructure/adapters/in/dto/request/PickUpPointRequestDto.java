package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Pickup point requested for the route")
public class PickUpPointRequestDto {

    @NotBlank(message = "passengerId cannot be blank")
    @Schema(description = "Id of the passenger to pick up", example = "passenger-123")
    private String passengerId;

    @NotNull(message = "location cannot be null")
    @Valid
    @Schema(description = "Location of the pickup point")
    private LocationRequestDto location;

    @NotNull(message = "geofence configuration cannot be null")
    @Valid
    @Schema(description = "Geofence configuration for the pickup point")
    private GeofenceRequestDto geofenceConfig;

}

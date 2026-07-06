package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Pickup point requested for the route")
public class PickUpPointRequestDto {

    @NotBlank(message = "destination cannot be empty")
    @NotNull(message = "origin cannot be null")
    @Schema(description = "Id of the passenger to pick up", example = "passenger-123")
    private String passengerId;

    @NotEmpty(message = "location cannot be empty")
    @NotNull(message = "location cannot be null")
    @Schema(description = "Location of the pickup point")
    private LocationRequestDto location;

    @NotEmpty(message = "geofence configuration cannot be empty")
    @NotNull(message = "geofence configuration cannot be null")
    @Schema(description = "Geofence configuration for the pickup point")
    private GeofenceRequestDto geofenceConfig;

}

package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(description = "Geofence radius configuration for a pickup point")
public class GeofenceRequestDto {

    @DecimalMin(value = "1.0", message = "radiusMeters must be at least 1 meter")
    @Schema(description = "Geofence radius in meters", example = "50")
    private double radiusMeters;

}

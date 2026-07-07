package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.GeofenceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Geofence status of a pickup point")
public class GeofenceResponseDto {

    @Schema(description = "Geofence radius in meters", example = "50")
    private double radiusMeters;

    @Schema(description = "Moment at which the geofence was triggered")
    private LocalDateTime triggeredAt;

    @Schema(description = "Current status of the geofence")
    private GeofenceStatus geofenceStatus;

}

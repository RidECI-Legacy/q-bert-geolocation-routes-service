package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.PickUpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Pickup point within a route")
public class PickUpPointResponseDto {

    @Schema(description = "Id of the passenger to pick up", example = "passenger-123")
    private String passengerId;

    @Schema(description = "Estimated pickup time for the passenger")
    private LocalDateTime estimatedPickUpTime;

    @Schema(description = "Location of the pickup point")
    private LocationResponseDto location;

    @Schema(description = "Geofence configuration for the pickup point")
    private GeofenceResponseDto geofenceConfig;

    @Schema(description = "Current status of the pickup point")
    private PickUpStatus pickUpStatus;

}

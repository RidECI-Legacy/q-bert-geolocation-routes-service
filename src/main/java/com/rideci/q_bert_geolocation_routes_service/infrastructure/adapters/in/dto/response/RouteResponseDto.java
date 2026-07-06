package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Geolocation route")
public class RouteResponseDto {

    @Schema(description = "Id of the route", example = "3f9a1c2e-...")
    private String id;

    @Schema(description = "Id of the trip associated with the route", example = "trip-123")
    private String tripId;

    @Schema(description = "Origin point of the route")
    private LocationResponseDto origin;

    @Schema(description = "Destination point of the route")
    private LocationResponseDto destination;

    @Schema(description = "Total distance of the route in meters")
    private double totalDistance;

    @Schema(description = "Remaining distance to travel in meters")
    private double remainingDistance;

    @Schema(description = "Estimated arrival time at the destination")
    private LocalDateTime estimatedArrivalTime;

    @Schema(description = "Encoded polyline of the route")
    private String polyline;

    @Schema(description = "Pickup points of the route, already optimized")
    private List<PickUpPointResponseDto> pickUpPoints;

    @Schema(description = "Creation date of the route")
    private LocalDateTime createdAt;

    @Schema(description = "Last update date of the route")
    private LocalDateTime updatedAt;

}

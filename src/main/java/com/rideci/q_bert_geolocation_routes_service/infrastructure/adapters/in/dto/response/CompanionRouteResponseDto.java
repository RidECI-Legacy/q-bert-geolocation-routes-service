package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Companion (public transit) route")
public class CompanionRouteResponseDto {

    @Schema(description = "Id of the companion route", example = "3f9a1c2e-...")
    private String id;

    @Schema(description = "Id of the trip associated with the companion route", example = "trip-123")
    private String tripId;

    @Schema(description = "Origin point of the companion route")
    private LocationResponseDto origin;

    @Schema(description = "Destination point of the companion route")
    private LocationResponseDto destination;

    @Schema(description = "Departure time used to plan the itinerary")
    private LocalDateTime departureTime;

    @Schema(description = "Estimated arrival time at the destination")
    private LocalDateTime estimatedArrivalTime;

    @Schema(description = "Total distance of the itinerary in meters")
    private double totalDistance;

    @Schema(description = "Number of transfers in the itinerary")
    private int totalTransfers;

    @Schema(description = "Legs (walk/bus segments) of the itinerary, in order")
    private List<TransitLegResponseDto> legs;

    @Schema(description = "Creation date of the companion route")
    private LocalDateTime createdAt;

    @Schema(description = "Last update date of the companion route")
    private LocalDateTime updatedAt;

}

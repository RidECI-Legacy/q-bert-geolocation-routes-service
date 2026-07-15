package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.Duration;
import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.LegMode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "A leg (walk or bus segment) of a companion route")
public class TransitLegResponseDto {

    @Schema(description = "Id of the leg", example = "leg-1")
    private String id;

    @Schema(description = "Order of this leg within the itinerary", example = "1")
    private int sequence;

    @Schema(description = "Mode of transport for this leg")
    private LegMode mode;

    @Schema(description = "Origin point of the leg")
    private LocationResponseDto origin;

    @Schema(description = "Destination point of the leg")
    private LocationResponseDto destination;

    @Schema(description = "GTFS stop id where this leg starts, null for WALK legs", example = "stop-4521")
    private String originStopId;

    @Schema(description = "GTFS stop id where this leg ends, null for WALK legs", example = "stop-8890")
    private String destinationStopId;

    @Schema(description = "Short name of the bus line, null for WALK legs", example = "T50")
    private String routeShortName;

    @Schema(description = "Operating agency, null for WALK legs", example = "TransMilenio")
    private String agencyName;

    @Schema(description = "GTFS route id, null for WALK legs")
    private String gtfsRouteId;

    @Schema(description = "GTFS trip id of the specific scheduled vehicle run, null for WALK legs")
    private String gtfsTripId;

    @Schema(description = "Destination displayed on the bus, null for WALK legs")
    private String headsign;

    @Schema(description = "Encoded polyline of the leg geometry")
    private String polyline;

    @Schema(description = "Distance of the leg in meters")
    private double distance;

    @Schema(description = "Duration of the leg")
    private Duration duration;

    @Schema(description = "Departure time of the leg")
    private LocalDateTime departureTime;

    @Schema(description = "Arrival time of the leg")
    private LocalDateTime arrivalTime;

}

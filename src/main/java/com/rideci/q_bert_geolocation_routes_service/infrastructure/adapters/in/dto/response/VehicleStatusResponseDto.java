package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.VehicleStopStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Live position and ETA of the vehicle serving a bus leg")
public class VehicleStatusResponseDto {

    @Schema(description = "GTFS trip id of the vehicle's scheduled run")
    private String gtfsTripId;

    @Schema(description = "GTFS route id of the bus line")
    private String gtfsRouteId;

    @Schema(description = "Current latitude of the vehicle")
    private double latitude;

    @Schema(description = "Current longitude of the vehicle")
    private double longitude;

    @Schema(description = "Current heading of the vehicle in degrees, if available")
    private Double bearing;

    @Schema(description = "Status of the vehicle relative to its next stop")
    private VehicleStopStatus vehicleStopStatus;

    @Schema(description = "Moment this position was reported")
    private LocalDateTime positionTimestamp;

    @Schema(description = "Estimated arrival time at the boarding stop")
    private LocalDateTime estimatedArrivalAtStop;

    @Schema(description = "Current delay in seconds, if available")
    private Integer delaySeconds;

}

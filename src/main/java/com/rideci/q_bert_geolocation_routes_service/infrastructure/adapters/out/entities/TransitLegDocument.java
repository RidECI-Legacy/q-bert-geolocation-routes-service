package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.Duration;
import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.LegMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransitLegDocument {

    private String id;

    private int sequence;

    private LegMode mode;

    private LocationDocument origin;

    private LocationDocument destination;

    private String originStopId;

    private String destinationStopId;

    private String routeShortName;

    private String agencyName;

    private String gtfsRouteId;

    private String gtfsTripId;

    private String headsign;

    private String polyline;

    private double distance;

    private Duration duration;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

}

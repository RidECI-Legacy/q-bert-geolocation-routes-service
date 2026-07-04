package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.dto.tomtom;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TomTomRouteResponse(List<TomTomRoute> routes) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TomTomRoute(Summary summary, List<Leg> legs, List<OptimizedWaypoint> optimizedWaypoints) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Summary(long lengthInMeters, long travelTimeInSeconds, OffsetDateTime arrivalTime) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Leg(List<TomTomPoint> points) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TomTomPoint(double latitude, double longitude) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptimizedWaypoint(int providedIndex, int optimizedIndex) {
    }

}

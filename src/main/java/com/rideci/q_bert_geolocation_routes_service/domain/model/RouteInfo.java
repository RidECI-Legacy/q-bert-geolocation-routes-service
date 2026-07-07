package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RouteInfo {
    
    private double totalDistance;

    private double totalDuration;

    private LocalDateTime estimatedArrivalTime;

    private String polyline;

}

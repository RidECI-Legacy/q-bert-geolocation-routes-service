package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Route {
  
    private String id;

    private String tripId;

    private Location origin;

    private Location destination;

    private double totalDistance;

    private double remainingDistance;

    private LocalDateTime estimatedArrivalTime;

    private String polyline;

    private List<PickUpPoint> pickupPoints;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

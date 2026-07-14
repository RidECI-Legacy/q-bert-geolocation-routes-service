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
@AllArgsConstructor
@NoArgsConstructor
public class CompanionRoute {

    private String id;

    private String tripId;

    private Location origin;

    private Location destination;

    private LocalDateTime departureTime;

    private LocalDateTime estimatedArrivalTime;

    private double totalDistance;

    private int totalTransfers;

    private List<TransitLeg> legs;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

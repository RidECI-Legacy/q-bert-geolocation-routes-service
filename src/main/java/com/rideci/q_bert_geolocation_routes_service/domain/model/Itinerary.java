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
public class Itinerary {

    private String id;

    private LocalDateTime estimatedArrivalTime;

    private double totalDistance;

    private double remainingDistance;

    private int totalTransfers;

    private List<Leg> legs;
    
}

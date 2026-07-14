package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransitItinerary {

    private List<TransitLeg> legs;

    private double totalDistance;

    private int totalTransfers;

    private LocalDateTime estimatedArrivalTime;

}

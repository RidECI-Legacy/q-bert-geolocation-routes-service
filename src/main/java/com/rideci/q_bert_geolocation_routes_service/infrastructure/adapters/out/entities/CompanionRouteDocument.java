package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "companion-routes")
public class CompanionRouteDocument {

    @Id
    private String id;

    private String tripId;

    private LocationDocument origin;

    private LocationDocument destination;

    private LocalDateTime departureTime;

    private LocalDateTime estimatedArrivalTime;

    private double totalDistance;

    private int totalTransfers;

    private List<TransitLegDocument> legs;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

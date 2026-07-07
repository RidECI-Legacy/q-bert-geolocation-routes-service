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
@Document(collection = "routes")
public class RouteDocument {

    @Id
    private String id;

    private String tripId;

    private LocationDocument origin;

    private LocationDocument destination;
    
    private double totalDistance;

    private double remainingDistance;

    private LocalDateTime estimatedArrivalTime;

    private String polyline;

    private List<PickUpPointDocument> pickUpPoints;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
}

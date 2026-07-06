package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RouteResponseDto {
    
    private String id;

    private String tripId;

    private LocationResponseDto origin;

    private LocationResponseDto destination;

    private double totalDistance;

    private double remainingDistance;

    private LocalDateTime estimatedArrivalTime;

    private String polyline;

    private List<PickUpPointResponseDto> pickupPoints;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}

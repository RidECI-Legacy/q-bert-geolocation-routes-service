package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

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
public class PlanRoute {

    private String id;

    private String tripId;

    private Location origin;

    private Location destination;

    private String polyline;

    private TravelTracking travelTracking;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

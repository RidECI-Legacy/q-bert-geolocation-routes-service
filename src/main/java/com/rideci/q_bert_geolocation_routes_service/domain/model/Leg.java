package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.LegMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leg {

    private String id;

    private Location origin;

    private Location destination;

    private LocalDateTime duration;

    private double distance;

    private String routeName;

    private LegMode mode;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;
    
}

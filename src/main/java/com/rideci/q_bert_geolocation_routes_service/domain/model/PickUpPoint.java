package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.PickUpStatus;

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
public class PickUpPoint {
    
    private String PassengerId;

    private LocalDateTime estimatedPickUpTime;

    private Location location;

    private Geofence geofenceConfig;
    
    private PickUpStatus pickUpStatus;

    private int order;

}

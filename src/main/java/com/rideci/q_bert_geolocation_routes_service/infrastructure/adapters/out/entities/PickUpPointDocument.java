package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.PickUpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickUpPointDocument {

    private String passengerId;

    private LocalDateTime estimatedPickUpTime;

    private LocationDocument location;

    private GeofenceDocument geofenceConfig;

    private PickUpStatus pickUpStatus;

}

package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.PickUpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickUpPointDocument {
    
    private String passengerId;

    private LocalDateTime estimatedPickUpTime;

    private LocationDocument location;

    private PickUpStatus pickUpStatus;

    private int order;

}

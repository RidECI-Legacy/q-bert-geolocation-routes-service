package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.PickUpStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PickUpPointResponseDto {
    
    private String passengerId;

    private LocalDateTime estimatedPickUpTime;

    private LocationResponseDto location;

    private GeofenceResponseDto geofenceConfig;
    
    private PickUpStatus pickUpStatus;

    private int order;

}

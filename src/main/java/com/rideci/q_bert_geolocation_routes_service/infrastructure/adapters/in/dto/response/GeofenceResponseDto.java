package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.GeofenceStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GeofenceResponseDto {

    private double radiusMeters;

    private LocalDateTime triggeredAt;
    
    private GeofenceStatus geofenceStatus;


}

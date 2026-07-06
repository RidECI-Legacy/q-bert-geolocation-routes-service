package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GeofenceRequestDto {

    private double radiusMeters;
    
}

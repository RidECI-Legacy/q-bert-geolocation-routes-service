package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PickUpPointRequestDto {
    
    private String passengerId;

    private LocationRequestDto location;

    private GeofenceRequestDto geofenceConfig;

}

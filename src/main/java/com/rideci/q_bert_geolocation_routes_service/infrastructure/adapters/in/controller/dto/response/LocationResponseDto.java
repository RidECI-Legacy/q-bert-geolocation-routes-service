package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationResponseDto {
    
    private double latitude;

    private double longitude;
    
    private LocalDateTime timestamp;
}

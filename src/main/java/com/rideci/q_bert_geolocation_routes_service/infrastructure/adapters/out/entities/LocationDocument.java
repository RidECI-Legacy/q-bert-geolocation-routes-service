package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDocument {

    private double latitude;

    private double longitude;
    
    private String timestamp;
    
}

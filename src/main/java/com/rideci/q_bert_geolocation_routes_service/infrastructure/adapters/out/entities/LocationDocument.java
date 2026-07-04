package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDocument {

    private double latitude;

    private double longitude;
    
    private String timestamp;
    
}

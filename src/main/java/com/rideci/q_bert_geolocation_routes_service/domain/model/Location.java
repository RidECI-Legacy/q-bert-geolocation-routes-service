package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    
    private double latitude;

    private double longitude;
    
    private LocalDateTime timestamp;
    
}

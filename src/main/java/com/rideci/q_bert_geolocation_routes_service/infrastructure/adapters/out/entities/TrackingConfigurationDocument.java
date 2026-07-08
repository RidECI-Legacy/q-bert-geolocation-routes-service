package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingConfigurationDocument {
 
    private String tripId;

    private String participantId;

    private int updateIntervalSeconds;

    private LocalDateTime updatedAt;

}

package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelTrackingDocument {
    
    private String tripId;

    private String participantId;

    private double speed;

    private double heading;

    private LocationDocument currentLocation;

    private ParticipantRole participantRole;

    private TrackingConfigurationDocument trackingConfiguration;

    private LocationShareDocument locationShare;

    private LocalDateTime updatedAt;

}

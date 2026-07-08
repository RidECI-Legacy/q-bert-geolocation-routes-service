package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "travel-tracking")
public class TravelTrackingDocument {
    
    private String tripId;

    private String participantId;

    private double speed;

    private double heading;

    private LocationDocument curreLocation;

    private ParticipantRole participantRole;

    private TrackingConfigurationDocument trackingConfiguration;

    private List<RouteHistoryDocument> routeReplay;

    private LocalDateTime updatedAt;

}

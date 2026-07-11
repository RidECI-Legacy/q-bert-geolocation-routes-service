package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ShareStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationShare {

    private String id;

    private String participantId;

    private String tripId;

    private String emergencyContactId;

    private Location currentLocation;

    private ShareStatus shareStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
}

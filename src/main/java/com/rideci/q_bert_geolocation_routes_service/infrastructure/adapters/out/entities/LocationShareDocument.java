package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.entities;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ShareStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "location-shares")
public class LocationShareDocument {

    @Id
    private String id;

    private String participantId;

    private String tripId;

    private String emergencyContactId;

    private LocationDocument currentLocation;

    private ShareStatus shareStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}


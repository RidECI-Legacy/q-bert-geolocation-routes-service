package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

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
public class TrackingConfiguration {

    private String tripId;

    private String participantId;

    private int updateIntervalSeconds;

    private LocalDateTime updatedAt;
}

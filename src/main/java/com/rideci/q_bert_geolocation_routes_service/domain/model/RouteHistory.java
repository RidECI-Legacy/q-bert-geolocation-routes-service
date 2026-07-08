package com.rideci.q_bert_geolocation_routes_service.domain.model;

import java.time.LocalDateTime;

import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ParticipantRole;

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
public class RouteHistory {

    private String id;

    private String tripId;

    private String participantId;

    private double speed;

    private double heading;

    private Location location;

    private ParticipantRole participantRole;

    private LocalDateTime recordedAt;
}

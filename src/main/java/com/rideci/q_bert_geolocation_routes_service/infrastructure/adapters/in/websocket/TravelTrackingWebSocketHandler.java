package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideci.q_bert_geolocation_routes_service.application.service.GeolocationService;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.TravelTrackingUpdateDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.TravelTrackingControllerMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TravelTrackingWebSocketHandler implements WebSocketHandler {

    private static final int TRIP_ID_SEGMENT = 3;
    private static final int PARTICIPANT_ID_SEGMENT = 5;

    private final GeolocationService geolocationService;
    private final TripLocationBroadcaster broadcaster;
    private final TravelTrackingControllerMapper travelTrackingControllerMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String[] segments = session.getHandshakeInfo().getUri().getPath().split("/");
        String tripId = segments[TRIP_ID_SEGMENT];
        String participantId = segments[PARTICIPANT_ID_SEGMENT];

        Flux<WebSocketMessage> outbound = broadcaster.subscribe(tripId)
                .map(travelTrackingControllerMapper::toResponse)
                .map(this::writeAsJson)
                .map(session::textMessage);

        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> processIncoming(payload, tripId, participantId)
                        .onErrorResume(error -> {
                            log.warn("Discarding invalid tracking update for trip {} participant {}", tripId,
                                    participantId, error);
                            return Mono.empty();
                        }))
                .then();

        return session.send(outbound).and(inbound);
    }

    private Mono<Void> processIncoming(String payload, String tripId, String participantId) {
        TravelTrackingUpdateDto update = readAsDto(payload);
        TravelTracking tracking = toDomain(update, tripId, participantId);

        return geolocationService.updateUserLocation(tripId, tracking)
                .doOnNext(updated -> broadcaster.publish(tripId, updated))
                .then();
    }

    private TravelTracking toDomain(TravelTrackingUpdateDto dto, String tripId, String participantId) {
        Location location = Location.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();

        return TravelTracking.builder()
                .tripId(tripId)
                .participantId(participantId)
                .speed(dto.getSpeed())
                .heading(dto.getHeading())
                .currentLocation(location)
                .participantRole(dto.getParticipantRole())
                .build();
    }

    private TravelTrackingUpdateDto readAsDto(String payload) {
        try {
            return objectMapper.readValue(payload, TravelTrackingUpdateDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid tracking update payload", e);
        }
    }

    private String writeAsJson(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize tracking update", e);
        }
    }

}

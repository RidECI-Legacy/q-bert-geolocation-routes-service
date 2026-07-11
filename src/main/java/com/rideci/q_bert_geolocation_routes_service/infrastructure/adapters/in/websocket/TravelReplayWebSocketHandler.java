package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideci.q_bert_geolocation_routes_service.application.service.GeolocationService;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.TravelTrackingControllerMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TravelReplayWebSocketHandler implements WebSocketHandler {

    private static final int TRIP_ID_SEGMENT = 3;
    private static final int PARTICIPANT_ID_SEGMENT = 5;
    private static final double DEFAULT_SPEED_MULTIPLIER = 1.0;

    private final GeolocationService geolocationService;
    private final TravelTrackingControllerMapper travelTrackingControllerMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        URI uri = session.getHandshakeInfo().getUri();
        String[] segments = uri.getPath().split("/");
        String tripId = segments[TRIP_ID_SEGMENT];
        String participantId = segments[PARTICIPANT_ID_SEGMENT];
        double speedMultiplier = extractSpeedMultiplier(uri);

        Flux<WebSocketMessage> outbound = geolocationService.getTravelReplay(tripId, participantId, speedMultiplier)
                .map(travelTrackingControllerMapper::toResponse)
                .map(this::writeAsJson)
                .map(session::textMessage);

        return session.send(outbound);
    }

    private double extractSpeedMultiplier(URI uri) {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
        String rawSpeed = queryParams.getFirst("speed");
        if (rawSpeed == null) {
            return DEFAULT_SPEED_MULTIPLIER;
        }

        try {
            double parsed = Double.parseDouble(rawSpeed);
            return parsed > 0 ? parsed : DEFAULT_SPEED_MULTIPLIER;
        } catch (NumberFormatException e) {
            return DEFAULT_SPEED_MULTIPLIER;
        }
    }

    private String writeAsJson(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize route history", e);
        }
    }

}

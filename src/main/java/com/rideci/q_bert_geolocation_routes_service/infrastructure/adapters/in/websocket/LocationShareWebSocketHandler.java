package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideci.q_bert_geolocation_routes_service.application.service.GeolocationService;
import com.rideci.q_bert_geolocation_routes_service.domain.model.LocationShare;
import com.rideci.q_bert_geolocation_routes_service.domain.model.enums.ShareStatus;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.TravelTrackingControllerMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Handles /ws/shares/{shareId}: the share id (returned by POST .../share) is the only
// credential needed to connect, so it works both for registered users and for an
// external contact who only has the link. Only the shared participant's location is
// relayed, not the rest of the trip's participants.
@Component
@RequiredArgsConstructor
public class LocationShareWebSocketHandler implements WebSocketHandler {

    private static final int SHARE_ID_SEGMENT = 3;

    private final GeolocationService geolocationService;
    private final TripLocationBroadcaster broadcaster;
    private final TravelTrackingControllerMapper travelTrackingControllerMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String shareId = session.getHandshakeInfo().getUri().getPath().split("/")[SHARE_ID_SEGMENT];

        return geolocationService.getLocationShare(shareId)
                .flatMap(share -> share.getShareStatus() == ShareStatus.ACTIVE
                        ? streamLocation(session, share)
                        : session.close(CloseStatus.POLICY_VIOLATION))
                .switchIfEmpty(session.close(CloseStatus.NOT_ACCEPTABLE));
    }

    private Mono<Void> streamLocation(WebSocketSession session, LocationShare share) {
        Flux<WebSocketMessage> outbound = broadcaster.subscribe(share.getTripId())
                .filter(tracking -> tracking.getParticipantId().equals(share.getParticipantId()))
                .map(travelTrackingControllerMapper::toResponse)
                .map(this::writeAsJson)
                .map(session::textMessage);

        return session.send(outbound);
    }

    private String writeAsJson(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize tracking update", e);
        }
    }

}

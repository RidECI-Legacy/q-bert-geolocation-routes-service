package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rideci.q_bert_geolocation_routes_service.application.service.CompanionRouteService;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.VehicleStatusNotAvailableException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitLeg;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.CompanionRouteControllerMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Handles /ws/companion-routes/{id}/legs/{legId}/vehicle-status: streams the live position/ETA
// of the specific vehicle serving a bus leg, outbound-only, sourced from BusRealtimeBroadcaster.
@Component
@RequiredArgsConstructor
public class BusRealtimeWebSocketHandler implements WebSocketHandler {

    private static final int COMPANION_ROUTE_ID_SEGMENT = 3;
    private static final int LEG_ID_SEGMENT = 5;

    private final CompanionRouteService companionRouteService;
    private final BusRealtimeBroadcaster broadcaster;
    private final CompanionRouteControllerMapper companionRouteControllerMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String[] segments = session.getHandshakeInfo().getUri().getPath().split("/");
        String companionRouteId = segments[COMPANION_ROUTE_ID_SEGMENT];
        String legId = segments[LEG_ID_SEGMENT];

        Flux<WebSocketMessage> outbound = companionRouteService.getCompanionRoute(companionRouteId)
                .flatMap(companionRoute -> findLeg(companionRoute, legId, companionRouteId))
                .flatMapMany(leg -> broadcaster.subscribe(leg.getGtfsTripId()))
                .map(companionRouteControllerMapper::toResponse)
                .map(this::writeAsJson)
                .map(session::textMessage);

        return session.send(outbound);
    }

    private Mono<TransitLeg> findLeg(CompanionRoute companionRoute, String legId, String companionRouteId) {
        return companionRoute.getLegs().stream()
                .filter(leg -> legId.equals(leg.getId()))
                .findFirst()
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new VehicleStatusNotAvailableException(companionRouteId, legId)));
    }

    private String writeAsJson(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to serialize vehicle status", e);
        }
    }

}

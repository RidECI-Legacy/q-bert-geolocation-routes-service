package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideci.q_bert_geolocation_routes_service.application.service.CompanionRouteService;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.VehicleStatusNotAvailableException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TransitLeg;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.VehicleStatusResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.CompanionRouteControllerMapper;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.websocket.BusRealtimeBroadcaster;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Tag(name = "Companion route realtime", description = """
        Point-in-time reads of a bus leg's live status. For a continuous live stream see \
        the /ws/companion-routes WebSocket endpoint.""")
@RestController
@RequestMapping("/api/v1/companion-routes/{id}/legs/{legId}")
@RequiredArgsConstructor
public class CompanionRouteRealtimeController {

    private static final Duration SNAPSHOT_TIMEOUT = Duration.ofSeconds(2);

    private final CompanionRouteService companionRouteService;
    private final BusRealtimeBroadcaster busRealtimeBroadcaster;
    private final CompanionRouteControllerMapper companionRouteControllerMapper;

    @Operation(summary = "Get the current live status of a bus leg")
    @GetMapping("/vehicle-status")
    public Mono<VehicleStatusResponseDto> getVehicleStatus(
            @Parameter(description = "Id of the companion route") @PathVariable String id,
            @Parameter(description = "Id of the leg to check") @PathVariable String legId) {
        return companionRouteService.getCompanionRoute(id)
                .flatMap(companionRoute -> findLeg(companionRoute, legId, id))
                .flatMap(leg -> busRealtimeBroadcaster.subscribe(leg.getGtfsTripId())
                        .next()
                        .timeout(SNAPSHOT_TIMEOUT)
                        .onErrorMap(TimeoutException.class, error -> new VehicleStatusNotAvailableException(id, legId)))
                .map(companionRouteControllerMapper::toResponse);
    }

    private Mono<TransitLeg> findLeg(CompanionRoute companionRoute, String legId, String companionRouteId) {
        return companionRoute.getLegs().stream()
                .filter(leg -> legId.equals(leg.getId()))
                .findFirst()
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new VehicleStatusNotAvailableException(companionRouteId, legId)));
    }

}

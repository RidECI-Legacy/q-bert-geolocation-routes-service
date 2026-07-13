package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideci.q_bert_geolocation_routes_service.application.service.GeolocationService;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.ShareLocationRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.UpdateTrackingIntervalRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.LocationShareResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.RouteHistoryResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TrackingConfigurationResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.TravelTrackingResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.TravelTrackingControllerMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Travel tracking", description = """
        Point-in-time reads of a trip's real-time tracking state. \
        For live updates and trip replay see the /ws/trips WebSocket endpoints.""")
@RestController
@RequestMapping("/api/v1/routes/{tripId}/tracking")
@RequiredArgsConstructor
public class TravelTrackingController {

    private final GeolocationService geolocationService;
    private final TravelTrackingControllerMapper travelTrackingControllerMapper;

    @Operation(summary = "Get the current location of a trip participant")
    @GetMapping("/{participantId}")
    public Mono<TravelTrackingResponseDto> getUserLocation(
            @Parameter(description = "Id of the trip") @PathVariable String tripId,
            @Parameter(description = "Id of the participant") @PathVariable String participantId) {
        return geolocationService.getUserLocation(tripId, participantId).map(travelTrackingControllerMapper::toResponse);
    }

    @Operation(summary = "Get the current location of every participant of a trip")
    @GetMapping
    public Flux<TravelTrackingResponseDto> getUsersLocation(
            @Parameter(description = "Id of the trip") @PathVariable String tripId) {
        return geolocationService.getUsersLocation(tripId).map(travelTrackingControllerMapper::toResponse);
    }

    @Operation(summary = "Replay the recorded route of a participant", description = """
            Returns the full recorded history of a participant's location, ordered by the moment \
            it was recorded. For a live, timed playback of this history use the \
            /ws/trips/{tripId}/replay/{participantId} WebSocket endpoint instead.""")
    @GetMapping("/{participantId}/replay")
    public Flux<RouteHistoryResponseDto> getTravelReplay(
            @Parameter(description = "Id of the trip") @PathVariable String tripId,
            @Parameter(description = "Id of the participant") @PathVariable String participantId) {
        return geolocationService.getTravelReplay(tripId, participantId, 0)
                .map(travelTrackingControllerMapper::toResponse);
    }

    @Operation(summary = "Update the location update interval of a trip participant")
    @PutMapping("/{participantId}/interval")
    public Mono<TrackingConfigurationResponseDto> updateConfigurableInterval(
            @Parameter(description = "Id of the trip") @PathVariable String tripId,
            @Parameter(description = "Id of the participant") @PathVariable String participantId,
            @Valid @RequestBody UpdateTrackingIntervalRequestDto request) {
        return geolocationService.updateConfigurableInterval(tripId, participantId, request.getUpdateIntervalSeconds())
                .map(travelTrackingControllerMapper::toResponse);
    }

    @Operation(summary = "Start sharing a participant's live location", description = """
            Creates a live-location share for a trip participant. The returned share id doubles \
            as the access token for the /ws/shares/{shareId} WebSocket, which streams that \
            participant's location for as long as the share stays active. Pass an \
            emergencyContactId to associate the share with a registered user, or omit it for \
            an external, link-only share.""")
    @PostMapping("/{participantId}/share")
    public ResponseEntity<Mono<LocationShareResponseDto>> shareLocation(
            @Parameter(description = "Id of the trip") @PathVariable String tripId,
            @Parameter(description = "Id of the participant whose location will be shared") @PathVariable String participantId,
            @RequestBody(required = false) ShareLocationRequestDto request) {
        String emergencyContactId = request != null ? request.getEmergencyContactId() : null;

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(geolocationService.shareLocation(tripId, participantId, emergencyContactId)
                        .map(travelTrackingControllerMapper::toResponse));
    }

}

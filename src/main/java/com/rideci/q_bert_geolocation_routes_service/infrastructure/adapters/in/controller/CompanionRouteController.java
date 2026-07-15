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

import com.rideci.q_bert_geolocation_routes_service.application.service.CompanionRouteService;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.CompanionRouteRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.CompanionRouteResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.CompanionRouteControllerMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Companion routes", description = "Creation, retrieval and updates of public transit itineraries for companions")
@RestController
@RequestMapping("/api/v1/companion-routes")
@RequiredArgsConstructor
public class CompanionRouteController {

    private final CompanionRouteService companionRouteService;
    private final CompanionRouteControllerMapper companionRouteControllerMapper;

    @Operation(summary = "Create a companion route", description = """
            Plans a public transit itinerary from an origin, destination and optional departure time \
            through OpenTripPlanner and persists it.""")
    @PostMapping
    public ResponseEntity<Mono<CompanionRouteResponseDto>> createCompanionRoute(
            @Valid @RequestBody CompanionRouteRequestDto companionRoute) {
        CompanionRoute newCompanionRoute = companionRouteControllerMapper.toDomain(companionRoute);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companionRouteService.createCompanionRoute(newCompanionRoute)
                        .map(companionRouteControllerMapper::toResponse));
    }

    @Operation(summary = "Update a companion route", description = """
            Updates the origin, destination and/or departure time of an existing companion route, \
            replanning the itinerary through OpenTripPlanner.""")
    @PutMapping("/{id}")
    public ResponseEntity<Mono<CompanionRouteResponseDto>> updateCompanionRoute(
            @Parameter(description = "Id of the companion route to update") @PathVariable String id,
            @Valid @RequestBody CompanionRouteRequestDto companionRoute) {
        CompanionRoute companionRouteToUpdate = companionRouteControllerMapper.toDomain(companionRoute);

        return ResponseEntity.ok(companionRouteService.updateCompanionRoute(id, companionRouteToUpdate)
                .map(companionRouteControllerMapper::toResponse));
    }

    @Operation(summary = "Get a companion route by id")
    @GetMapping("/{id}")
    public Mono<CompanionRouteResponseDto> getCompanionRoute(
            @Parameter(description = "Id of the companion route to retrieve") @PathVariable String id) {
        return companionRouteService.getCompanionRoute(id).map(companionRouteControllerMapper::toResponse);
    }

    @Operation(summary = "List all companion routes")
    @GetMapping
    public Flux<CompanionRouteResponseDto> getAllCompanionRoutes() {
        return companionRouteService.getAllCompanionRoutes().map(companionRouteControllerMapper::toResponse);
    }

}

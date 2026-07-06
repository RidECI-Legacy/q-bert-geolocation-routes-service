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
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.ErrorResponse;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.RouteRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.RouteResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.RouteControllerMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Routes", description = "Creation, retrieval and updates of geolocation routes")
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class GeolocationController {

    private final GeolocationService geolocationService;
    private final RouteControllerMapper routeControllerMapper;

    @Operation(summary = "Create a route", description = """
            Creates a new route from an origin, destination and pickup points. \
            The service calculates distance, estimated arrival time and polyline, \
            optimizing the order of the pickup points through TomTom.""")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Route created successfully",
                    content = @Content(schema = @Schema(implementation = RouteResponseDto.class))),
            @ApiResponse(responseCode = "502", description = "Failed to integrate with the geolocation provider (TomTom)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Mono<RouteResponseDto>> createRoute(@RequestBody RouteRequestDto route) {
        Route newRoute = routeControllerMapper.toDomain(route);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(geolocationService.createRoute(newRoute).map(routeControllerMapper::toResponse));
    }

    @Operation(summary = "Update a route", description = """
            Updates the origin, destination and/or pickup points of an existing route, \
            recalculating distance, ETA and polyline.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Route updated successfully",
                    content = @Content(schema = @Schema(implementation = RouteResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No route exists with the given id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Failed to integrate with the geolocation provider (TomTom)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Mono<RouteResponseDto>> updateRoute(
            @Parameter(description = "Id of the route to update") @PathVariable String id,
            @RequestBody RouteRequestDto route) {
        Route routeToUpdate = routeControllerMapper.toDomain(route);

        return ResponseEntity
                .ok(geolocationService.updateRoute(id, routeToUpdate).map(routeControllerMapper::toResponse));
    }

    @Operation(summary = "Get a route by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Route found",
                    content = @Content(schema = @Schema(implementation = RouteResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No route exists with the given id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Mono<RouteResponseDto> getRoute(
            @Parameter(description = "Id of the route to retrieve") @PathVariable String id) {
        return geolocationService.getRoute(id).map(routeControllerMapper::toResponse);
    }

    @Operation(summary = "List all routes")
    @ApiResponse(responseCode = "200", description = "List of routes",
            content = @Content(schema = @Schema(implementation = RouteResponseDto.class)))
    @GetMapping
    public Flux<RouteResponseDto> getAllRoutes() {
        return geolocationService.getAllRoutes().map(routeControllerMapper::toResponse);
    }

}

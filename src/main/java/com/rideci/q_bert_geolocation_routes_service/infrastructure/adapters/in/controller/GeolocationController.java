package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.controller;

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
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.request.RouteRequestDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.dto.response.RouteResponseDto;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.in.mapper.RouteControllerMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class GeolocationController {

    private final GeolocationService geolocationService;
    private final RouteControllerMapper routeControllerMapper;

    @PostMapping
    public ResponseEntity<Mono<RouteResponseDto>> createRoute(@RequestBody RouteRequestDto route) {
        Route newRoute = routeControllerMapper.toDomain(route);

        return ResponseEntity.ok(geolocationService.createRoute(newRoute).map(routeControllerMapper::toResponse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mono<RouteResponseDto>> updateRoute(@PathVariable String id,
            @RequestBody RouteRequestDto route) {
        Route routeToUpdate = routeControllerMapper.toDomain(route);

        return ResponseEntity
                .ok(geolocationService.updateRoute(id, routeToUpdate).map(routeControllerMapper::toResponse));
    }

    @GetMapping("/{id}")
    public Mono<RouteResponseDto> getRoute(@PathVariable String id) {
        return geolocationService.getRoute(id).map(routeControllerMapper::toResponse);
    }

    @GetMapping
    public Flux<RouteResponseDto> getAllRoutes() {
        return geolocationService.getAllRoutes().map(routeControllerMapper::toResponse);
    }

}

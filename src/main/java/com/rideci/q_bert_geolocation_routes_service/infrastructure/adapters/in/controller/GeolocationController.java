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

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateRouteUseCase;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class GeolocationController {

    private final CreateRouteUseCase createRouteUseCase;
    private final UpdateRouteUseCase updateRouteUseCase;
    private final GetRouteUseCase getRouteUseCase;
    private final GetAllRoutesUseCase getAllRoutesUseCase;

    @PostMapping
    public Mono<ResponseEntity<Route>> createRoute(@RequestBody Route route) {
        return createRouteUseCase.createRoute(route)
                .map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
    }

    @PutMapping("/{id}")
    public Mono<Route> updateRoute(@PathVariable String id, @RequestBody Route route) {
        return updateRouteUseCase.updateRoute(id, route);
    }

    @GetMapping("/{id}")
    public Mono<Route> getRoute(@PathVariable String id) {
        return getRouteUseCase.getRoute(id);
    }

    @GetMapping
    public Flux<Route> getAllRoutes() {
        return getAllRoutesUseCase.getAllRoutes();
    }

}

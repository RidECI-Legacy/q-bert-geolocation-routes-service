package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import org.springframework.stereotype.Repository;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;
import com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.mapper.RouteMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Repository
public class GeolocationRepositoryAdapter implements GeolocationRepositoryOutPort {

    private final GeolocationRepository geolocationRepository;
    private final RouteMapper routeMapper;

    @Override
    public Mono<Route> save(Route route) {
        return geolocationRepository.save(routeMapper.toDocument(route))
                .map(routeMapper::toDomain);
    }

    @Override
    public Mono<Route> findRouteById(String id) {
        return geolocationRepository.findById(id)
                .map(routeMapper::toDomain);
    }

    @Override
    public Flux<Route> findAllRoutes() {
        return geolocationRepository.findAll()
                .map(routeMapper::toDomain);
    }

}

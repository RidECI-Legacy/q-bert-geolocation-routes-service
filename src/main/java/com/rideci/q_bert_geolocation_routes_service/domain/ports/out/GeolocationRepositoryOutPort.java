package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GeolocationRepositoryOutPort {

    Mono<Route> save(Route route);

    Mono<Route> update(String id, Route newRoute);

    Mono<Route> findRouteById(String id);

    Flux<Route> findAllRoutes();

}

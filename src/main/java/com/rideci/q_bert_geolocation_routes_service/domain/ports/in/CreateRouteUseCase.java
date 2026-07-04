package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;

import reactor.core.publisher.Mono;

public interface CreateRouteUseCase {

    Mono<Route> createRoute(Route route);

}

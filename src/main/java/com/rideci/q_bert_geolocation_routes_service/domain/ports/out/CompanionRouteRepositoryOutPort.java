package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CompanionRouteRepositoryOutPort {

    Mono<CompanionRoute> save(CompanionRoute companionRoute);

    Mono<CompanionRoute> update(String id, CompanionRoute companionRoute);

    Mono<CompanionRoute> findCompanionRouteById(String id);

    Flux<CompanionRoute> findAllCompanionRoutes();

}

package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;

import reactor.core.publisher.Mono;

public interface UpdateCompanionRouteUseCase {

    Mono<CompanionRoute> updateCompanionRoute(String id, CompanionRoute companionRoute);

}

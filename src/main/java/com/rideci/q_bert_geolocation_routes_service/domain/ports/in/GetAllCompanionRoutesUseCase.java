package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;

import reactor.core.publisher.Flux;

public interface GetAllCompanionRoutesUseCase {

    Flux<CompanionRoute> getAllCompanionRoutes();

}

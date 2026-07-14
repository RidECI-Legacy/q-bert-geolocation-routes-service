package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateCompanionRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.CompanionRouteRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class UpdateCompanionRouteUseCaseImpl implements UpdateCompanionRouteUseCase {

    private final CompanionRouteRepositoryOutPort companionRouteRepositoryOutPort;

    @Override
    public Mono<CompanionRoute> updateCompanionRoute(String id, CompanionRoute companionRoute) {
        return companionRouteRepositoryOutPort.update(id, companionRoute);
    }

}

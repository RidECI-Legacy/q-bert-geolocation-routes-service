package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetCompanionRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.CompanionRouteRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class GetCompanionRouteUseCaseImpl implements GetCompanionRouteUseCase {

    private final CompanionRouteRepositoryOutPort companionRouteRepositoryOutPort;

    @Override
    public Mono<CompanionRoute> getCompanionRoute(String id) {
        return companionRouteRepositoryOutPort.findCompanionRouteById(id);
    }

}

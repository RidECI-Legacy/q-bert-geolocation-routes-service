package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.CompanionRoute;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllCompanionRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.CompanionRouteRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@UseCase
@RequiredArgsConstructor
public class GetAllCompanionRoutesUseCaseImpl implements GetAllCompanionRoutesUseCase {

    private final CompanionRouteRepositoryOutPort companionRouteRepositoryOutPort;

    @Override
    public Flux<CompanionRoute> getAllCompanionRoutes() {
        return companionRouteRepositoryOutPort.findAllCompanionRoutes();
    }

}

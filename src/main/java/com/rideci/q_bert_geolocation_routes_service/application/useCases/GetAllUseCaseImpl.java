package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@UseCase
@RequiredArgsConstructor
public class GetAllUseCaseImpl implements GetAllRoutesUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Flux<Route> getAllRoutes() {
        return geolocationRepositoryOutPort.findAllRoutes();
    }

}

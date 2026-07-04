package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.exception.RouteNotFoundException;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class GetRouteUseCaseImpl implements GetRouteUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Mono<Route> getRoute(String routeId) {
        return geolocationRepositoryOutPort.findRouteById(routeId)
                .switchIfEmpty(Mono.error(() -> new RouteNotFoundException(routeId)));
    }

}

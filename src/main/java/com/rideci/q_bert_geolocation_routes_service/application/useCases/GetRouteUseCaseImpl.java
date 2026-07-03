package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import java.util.Optional;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetRouteUseCaseImpl implements GetRouteUseCase {

    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Optional<Route> getRoute(String routeId) {
        return geolocationRepositoryOutPort.findRouteById(routeId);
    }
    
}

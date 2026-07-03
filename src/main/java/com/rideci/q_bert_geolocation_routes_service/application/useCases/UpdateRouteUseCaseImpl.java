package com.rideci.q_bert_geolocation_routes_service.application.useCases;

import java.util.Optional;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UpdateRouteUseCaseImpl implements UpdateRouteUseCase {
    
    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;


    @Override
    public Optional<Route> updateRoute(String routeId, Route updatedRoute) {
        return geolocationRepositoryOutPort.updateRoute(updatedRoute);
    }
    
}

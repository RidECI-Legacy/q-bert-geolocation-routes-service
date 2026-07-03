package com.rideci.q_bert_geolocation_routes_service.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.CreateRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetAllRoutesUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.GetRouteUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateRouteUseCase;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeolocationService implements CreateRouteUseCase, UpdateRouteUseCase, GetRouteUseCase, GetAllRoutesUseCase {
    
    private final CreateRouteUseCase createRouteUseCase;
    private final UpdateRouteUseCase updateRouteUseCase;
    private final GetRouteUseCase getRouteUseCase;
    private final GetAllRoutesUseCase getAllRoutesUseCase;

    @Override
    public Route createRoute(Route route) {
        return createRouteUseCase.createRoute(route);
    }

    @Override
    public Optional<Route> updateRoute(String routeId, Route updatedRoute) {
        return updateRouteUseCase.updateRoute(routeId, updatedRoute);
    }

    @Override
    public Optional<Route> getRoute(String routeId) {
        return getRouteUseCase.getRoute(routeId);
    }

    @Override
    public Optional<List<Route>> getAllRoutes() {
        return getAllRoutesUseCase.getAllRoutes();
    }

}

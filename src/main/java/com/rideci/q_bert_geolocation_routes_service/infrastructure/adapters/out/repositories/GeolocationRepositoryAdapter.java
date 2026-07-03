package com.rideci.q_bert_geolocation_routes_service.infrastructure.adapters.out.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
@Repository
public class GeolocationRepositoryAdapter implements GeolocationRepositoryOutPort {

    private final GeolocationRepository geolocationRepository;

    @Override
    public Route saveRoute(Route route) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveRoute'");
    }

    @Override
    public Optional<Route> updateRoute(Route newRoute) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateRoute'");
    }

    @Override
    public Optional<Route> findRouteById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findRouteById'");
    }

    @Override
    public Optional<List<Route>> findAllRoutes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllRoutes'");
    }
    
}

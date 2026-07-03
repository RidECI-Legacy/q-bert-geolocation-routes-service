package com.rideci.q_bert_geolocation_routes_service.domain.ports.out;

import java.util.List;
import java.util.Optional;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;

public interface GeolocationRepositoryOutPort {
    
    Route saveRoute(Route route);

    Optional<Route> updateRoute(Route newRoute);

    Optional<Route> findRouteById(String id);

    Optional<List<Route>> findAllRoutes();

}

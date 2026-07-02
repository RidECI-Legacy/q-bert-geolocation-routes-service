package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import java.util.Optional;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;

public interface UpdateRouteUseCase {
    
    Optional<Route> updateRoute(String routeId, Route updatedRoute);

}

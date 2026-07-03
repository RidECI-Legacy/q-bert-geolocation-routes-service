package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;

public interface CreateRouteUseCase {
    
    Route createRoute(Route route);

}

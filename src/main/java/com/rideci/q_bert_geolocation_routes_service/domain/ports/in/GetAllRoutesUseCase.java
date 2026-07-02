package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import java.util.List;
import java.util.Optional;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;

public interface GetAllRoutesUseCase {
    
    Optional<List<Route>> getAllRoutes();
    
}

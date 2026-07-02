package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import java.util.List;

import com.rideci.q_bert_geolocation_routes_service.domain.model.Location;
import com.rideci.q_bert_geolocation_routes_service.domain.model.PickupPoint;
import com.rideci.q_bert_geolocation_routes_service.domain.model.Route;

public interface CreateRouteUseCase {
    
    Route createRoute(String tripId, Location origin, Location destination, List<PickupPoint> pickupPoints);

}

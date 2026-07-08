package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;

import reactor.core.publisher.Mono;

public interface UpdateUserLocationUseCase {
    
    Mono<TravelTracking> updateUsersLocationUseCase(String tripId, TravelTracking newUsertracking);

}

package com.rideci.q_bert_geolocation_routes_service.domain.ports.in;

import com.rideci.q_bert_geolocation_routes_service.domain.model.LocationShare;

import reactor.core.publisher.Mono;

public interface ShareLocationUseCase {

    Mono<LocationShare> shareLocation(String tripId, String passengerId, String emergencyContactId);
    
}

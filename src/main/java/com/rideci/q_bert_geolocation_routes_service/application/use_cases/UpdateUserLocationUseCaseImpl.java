package com.rideci.q_bert_geolocation_routes_service.application.use_cases;

import com.rideci.q_bert_geolocation_routes_service.application.annotation.UseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.model.TravelTracking;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.in.UpdateUserLocationUseCase;
import com.rideci.q_bert_geolocation_routes_service.domain.ports.out.GeolocationRepositoryOutPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class UpdateUserLocationUseCaseImpl implements UpdateUserLocationUseCase {
    
    private final GeolocationRepositoryOutPort geolocationRepositoryOutPort;

    @Override
    public Mono<TravelTracking> updateUserLocation(String tripId, TravelTracking newUsertracking) {
        return geolocationRepositoryOutPort.updateUserLocation(tripId, newUsertracking);
    }

}
